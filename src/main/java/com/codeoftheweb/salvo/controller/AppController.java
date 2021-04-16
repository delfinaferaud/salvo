package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.codeoftheweb.salvo.controller.Util.makeMap;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    GamePlayerRepository gamePlayerRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    ScoreRepository scoreRepository;

    @Autowired
    ShipRepository shipRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/game_view/{nn}")
    public ResponseEntity<Map<String, Object>> getGameViewByGamePlayerID(@PathVariable Long nn, Authentication authentication) {
        Player player;
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "Paso algo"), HttpStatus.UNAUTHORIZED);
        }

        if (playerRepository.findByUserName(authentication.getName()) instanceof Player) {
            player = playerRepository.findByUserName(authentication.getName());
        } else {
            player = null;
        }
        GamePlayer gamePlayer = gamePlayerRepository.findById(nn).orElse(null);

        if (player == null) {
            return new ResponseEntity<>(makeMap("error", "Paso algo"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer == null) {
            return new ResponseEntity<>(makeMap("error", "Paso algo"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != player.getId()) {
            return new ResponseEntity<>(makeMap("error", "Paso algo"), HttpStatus.CONFLICT);
        }
        Map<String, Object> dto = new LinkedHashMap<>();
        Map<String, Object> hits = new LinkedHashMap<>();
        GamePlayer opponent = gamePlayer.opponent();
        if (opponent.getId() != 0) {
            hits.put("self", hitsAndSinks(gamePlayer, opponent));
            hits.put("opponent", hitsAndSinks(opponent, gamePlayer));
        } else {
            hits.put("self", new ArrayList<>());
            hits.put("opponent", new ArrayList<>());
        }


        dto.put("id", gamePlayer.getGame().getId());
        dto.put("created", gamePlayer.getGame().getCreatedDate());
        dto.put("gameState", gameState(gamePlayer));

        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers()
                .stream()
                .map(gamePlayer1 -> gamePlayer1.gamePlayerDTO())
                .collect(Collectors.toList()));
        dto.put("ships", gamePlayer.getShips()
                .stream()
                .map(ship -> ship.shipDTO())
                .collect(Collectors.toList()));
        dto.put("salvoes", gamePlayer.getGame().getGamePlayers()
                .stream()
                .flatMap(gamePlayer1 -> gamePlayer1.getSalvoes()
                        .stream()
                        .map(salvo -> salvo.SalvoDTO()))
                .collect(Collectors.toList()));
        dto.put("hits", hits);


        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private List<Map> hitsAndSinks(GamePlayer gamePlayer, GamePlayer oponente) {

        List<Map> totalMap = new ArrayList<>();

        int carrierDamage = 0;
        int battleshipDamage = 0;
        int submarineDamage = 0;
        int destroyerDamage = 0;
        int patrolboatDamage = 0;

        List<String> carrierLocation = findShipLocations(gamePlayer, "carrier");
        List<String> battleshipLocation = findShipLocations(gamePlayer, "battleship");
        List<String> submarineLocation = findShipLocations(gamePlayer, "submarine");
        List<String> destroyerLocation = findShipLocations(gamePlayer, "destroyer");
        List<String> patrolboatLocation = findShipLocations(gamePlayer, "patrolboat");


        for (Salvo salvo : oponente.getSalvoes()) {
            // opponent salvoes are analyzed in order to see which salvoes match with opponent ship locations

            Map<String, Object> map = new LinkedHashMap<>();
            Map<String, Object> map2 = new LinkedHashMap<>();

            // hits in turn counter
            int carrierHitsInTurn = 0;
            int battleshipHitsInTurn = 0;
            int submarineHitsInTurn = 0;
            int destroyerHitsInTurn = 0;
            int patrolboatHitsInTurn = 0;

            ArrayList<String> hitCellsList = new ArrayList<>(); // cells with the locations of the ships hit
            int missedShots = salvo.getSalvoLocations().size(); // missed shots

            for (String salvoLocation : salvo.getSalvoLocations()) {
                // the locations of each salvo are traversed to see which locations match enemy ship locations

                if (carrierLocation.contains(salvoLocation)) {
                    carrierDamage++;
                    carrierHitsInTurn++;
                    hitCellsList.add(salvoLocation);
                    missedShots--;
                }
                if (battleshipLocation.contains(salvoLocation)) {
                    battleshipDamage++;
                    battleshipHitsInTurn++;
                    hitCellsList.add(salvoLocation);
                    missedShots--;
                }
                if (submarineLocation.contains(salvoLocation)) {
                    submarineDamage++;
                    submarineHitsInTurn++;
                    hitCellsList.add(salvoLocation);
                    missedShots--;
                }
                if (destroyerLocation.contains(salvoLocation)) {
                    destroyerDamage++;
                    destroyerHitsInTurn++;
                    hitCellsList.add(salvoLocation);
                    missedShots--;
                }
                if (patrolboatLocation.contains(salvoLocation)) {
                    patrolboatDamage++;
                    patrolboatHitsInTurn++;
                    hitCellsList.add(salvoLocation);
                    missedShots--;
                }
            }

            // turns where a hit occurred
            map2.put("carrierHits", carrierHitsInTurn);
            map2.put("battleshipHits", battleshipHitsInTurn);
            map2.put("submarineHits", submarineHitsInTurn);
            map2.put("destroyerHits", destroyerHitsInTurn);
            map2.put("patrolboatHits", patrolboatHitsInTurn);

            //amount of damage each ship received
            map2.put("carrier", carrierDamage);
            map2.put("battleship", battleshipDamage);
            map2.put("submarine", submarineDamage);
            map2.put("destroyer", destroyerDamage);
            map2.put("patrolboat", patrolboatDamage);

            map.put("turn", salvo.getTurn());
            map.put("hitLocations", hitCellsList);
            map.put("damages", map2);
            map.put("missed", missedShots);

            totalMap.add(map);


        }
        return totalMap;
    }


    public List<String> findShipLocations(GamePlayer gamePlayer, String type) {
        Optional<Ship> response;
        response = gamePlayer.getShips().stream().filter(ship -> ship.getType().equals(type)).findFirst();
        if (response.isEmpty()) {
            return new ArrayList<String>();
        }
        return response.get().getLocations();
    }


    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;

    }

    private Boolean getIfAllSunk(GamePlayer self, GamePlayer opponent) {
        if (!opponent.getShips().isEmpty() && !self.getSalvoes().isEmpty()) {
            return opponent.getSalvoes().stream().flatMap(salvo -> salvo.getSalvoLocations()
                    .stream()).collect(Collectors.toList())
                    .containsAll(self.getShips().stream().flatMap(ship -> ship.getLocations()
                            .stream()).collect(Collectors.toList()));

        }
        return false;
    }


    private String gameState(GamePlayer gamePlayer) {

        GamePlayer opp = gamePlayer.opponent();

        if (gamePlayer.getShips().size() == 0) {
            return "PLACESHIPS";
        }
        if (gamePlayer.getGame().getGamePlayers().size() == 1) {
            return "WAITINGFOROPP";
        }

        if (gamePlayer.getGame().getGamePlayers().size() == 2) {

            if (gamePlayer.getSalvoes().size() == opp.getSalvoes().size()) {

                if ((getIfAllSunk(opp, gamePlayer) && !getIfAllSunk(gamePlayer, opp))) {
                    scoreRepository.save(new Score(1.0, LocalDateTime.now(), gamePlayer.getPlayer(), gamePlayer.getGame()));

                    return "WON";
                }
                if (getIfAllSunk(opp, gamePlayer) && getIfAllSunk(gamePlayer, opp)) {
                    scoreRepository.save(new Score(0.5, LocalDateTime.now(), gamePlayer.getPlayer(), gamePlayer.getGame()));

                    return "TIE";
                }
                if (!getIfAllSunk(opp, gamePlayer) && getIfAllSunk(gamePlayer, opp)) {
                    scoreRepository.save(new Score(0.0, LocalDateTime.now(), gamePlayer.getPlayer(), gamePlayer.getGame()));

                    return "LOST";
                }
            }

            if (gamePlayer.getSalvoes().size() < opp.getSalvoes().size()) {
                return "PLAY";
            }

            if (gamePlayer.getSalvoes().size() == opp.getSalvoes().size() && (gamePlayer.getId() < opp.getId())) {
                return "PLAY";
            }

            if (gamePlayer.getSalvoes().size() > opp.getSalvoes().size()) {
                return "WAIT";
            }

            if (gamePlayer.getSalvoes().size() == opp.getSalvoes().size() && (gamePlayer.getId() > opp.getId())) {
                return "WAIT";
            }

        }
        return "UNDEFINED";

    }
}

