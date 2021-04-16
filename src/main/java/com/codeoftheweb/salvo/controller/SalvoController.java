package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.codeoftheweb.salvo.controller.Util.makeMap;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    ScoreRepository scoreRepository;

    @GetMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (!isGuest(authentication)) {
            dto.put("player", playerRepository.findByUserName(authentication.getName()).playerDTO());
        } else {
            dto.put("player", "Guest");
        }
        dto.put("games", gameRepository.findAll().stream().map(game -> game.gameDTO()).collect(Collectors.toList()));
        return dto;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;

    }


    @PostMapping("/games")  // adding the ability to create a game
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("error", "Player is unauthorized"), HttpStatus.UNAUTHORIZED);
            // gets the current user with the authentication object, and if there is none, it is sent an Unauthorized response
        } else {
            Game newGame = gameRepository.save(new Game(LocalDateTime.now()));
            Player thisPlayer = playerRepository.findByUserName(authentication.getName());
            GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(playerRepository.save(thisPlayer), newGame));
            // creates and saves a new game, and then saves a new gamePlayer for this game and the current user
            response = new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
            // and then sends a Created response, with json containing the new gamePlayer id
        }
        return response;

    }

    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)// adding the ability to join a game
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        Optional<Game> gameButton = gameRepository.findById(gameId);
        if (isGuest(authentication)) { // is a guest
            response = new ResponseEntity<>(makeMap("error", "Player is unauthorized"), HttpStatus.UNAUTHORIZED);
        } else if (!gameButton.isPresent()) {
            response = new ResponseEntity<>(makeMap("error", "gameId doesn't exist"), HttpStatus.FORBIDDEN);
        } else if (gameButton.get().getGamePlayers().size() > 1) {
            response = new ResponseEntity<>(makeMap("error", "sorry, game is full"), HttpStatus.FORBIDDEN);
        } else { // On the contrary, game has only one player
            Player player = playerRepository.findByUserName(authentication.getName());
            GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(player, gameButton.get()));
            response = new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
        }
        return response;
    }

    @RequestMapping(value = "/games/players/{gamePlayerID}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placedShips(@PathVariable Long gamePlayerID, @RequestBody Set<Ship> ships, Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerID);
        Player currentPlayer = playerRepository.findByUserName(authentication.getName());
        // a gamePlayer variable is declared to get rid of the .get()
        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("error", "Not allowed"), HttpStatus.UNAUTHORIZED);
        } else if (!gamePlayer.isPresent()) {
            response = new ResponseEntity<>(makeMap("error", "there is no game player with the given ID"), HttpStatus.UNAUTHORIZED);
        } else if (gamePlayer.get().getPlayer().getId() != currentPlayer.getId()) {
            // and if the gamePlayer doesn't match with the current user forbidden response is sent
            response = new ResponseEntity<>(makeMap("error", "the current user is not the game player the ID references"), HttpStatus.UNAUTHORIZED);
        } else if (gamePlayer.get().getShips().size() > 0) {
            response = new ResponseEntity<>(makeMap("error", "the user already has ships placed"), HttpStatus.FORBIDDEN);
        } else if (ships.size() > 0) {
            gamePlayer.get().addShips(ships);
            gamePlayerRepository.save(gamePlayer.get());
            //Otherwise, the ships should be added to the game player and saved, and a Created response should be sent.
            response = new ResponseEntity<>(makeMap("OK", "success"), HttpStatus.CREATED);
        } else {
            response = new ResponseEntity<>(makeMap("error", "you didn't send any ships"), HttpStatus.FORBIDDEN);
        }
        return response;
    }

    @RequestMapping(value = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSalvo(@PathVariable long gamePlayerId, @RequestBody Salvo salvo, Authentication authentication) {

        ResponseEntity<Map<String, Object>> respon = null;
        Player playerN = playerRepository.findByUserName(authentication.getName());
        Optional<GamePlayer> gamePlayerP = gamePlayerRepository.findById(gamePlayerId);
        GamePlayer otherPlayer = gamePlayerP.get().opponent();

        if (isGuest(authentication)) {
            respon = new ResponseEntity<>(makeMap("error", "there is no current user logged in"), HttpStatus.UNAUTHORIZED);
        } else if (!gamePlayerP.isPresent()) {
            respon = new ResponseEntity<>(makeMap("error", "there is no game player with the given ID"), HttpStatus.UNAUTHORIZED);
        } else if (gamePlayerP.get().getPlayer().getId() != playerN.getId()) {
            respon = new ResponseEntity<>(makeMap("error", "the current user is not the game player the ID references"), HttpStatus.UNAUTHORIZED);

        } else if (otherPlayer.getId() == 0 ){
            respon = new ResponseEntity<>(makeMap("error", "no opponent"), HttpStatus.UNAUTHORIZED);
        }else if (gamePlayerP.get().getSalvoes().size() <= otherPlayer.getSalvoes().size()){
            int turn = (gamePlayerP.get().getSalvoes().size()+1);
            if (repeatedTurn(gamePlayerP.get(),turn)){
                respon = new ResponseEntity<>(makeMap("error", "repited turn"), HttpStatus.FORBIDDEN);
            }else if (salvo.getSalvoLocations().size() > 5){
                respon = new ResponseEntity<>(makeMap("error", "only 5"), HttpStatus.FORBIDDEN);
            }else {
                salvo.setTurn(turn);
                salvo.setGamePlayers(gamePlayerP.get());
                gamePlayerP.get().addSalvo(salvo);
                salvoRepository.save(salvo);
                gamePlayerRepository.save(gamePlayerP.get());
                respon = new ResponseEntity<>(makeMap("OK", "Salvo created"), HttpStatus.CREATED);
            }
        }else {
            respon = new ResponseEntity<>(makeMap("error", "you have already a turn"), HttpStatus.FORBIDDEN);
        }
        return respon;
    }


    public static boolean repeatedTurn(GamePlayer gp, int salvoTurn) {
        boolean isRepeatedTurn = false;
        for (Salvo salvo : gp.getSalvoes()) {
            if (salvo.getTurn() == salvoTurn) {
                isRepeatedTurn = true;
                break;
            }
        }
        return isRepeatedTurn;
    }


    @GetMapping("/score")
    private Map<String, Object> getScore() {
        Map<String, Object> map = new HashMap<>();
        map.put("score", scoreRepository.findAll().stream().map(Score::scoreDTO).collect(Collectors.toList()));
        return map;
    }


    @GetMapping("/players")
    private List<Map<String, Object>> getPlayer() {
        return playerRepository.findAll().stream().map(player -> player.playerDTO()).collect(Collectors.toList());
    }

    @PostMapping("/players")
    public ResponseEntity<Map<String, Object>> register(@RequestParam String email, @RequestParam String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }
        if (playerRepository.findByUserName(email) != null) {
            return new ResponseEntity<>(makeMap("error", "Name in use"), HttpStatus.FORBIDDEN);
        }
        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(makeMap("message", "Success! Player created"), HttpStatus.CREATED);

    }

}