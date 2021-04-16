package com.codeoftheweb.salvo;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.*;
import static java.util.stream.Collectors.toList;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime createdDate;
    public boolean compareTurns;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany (mappedBy = "game", fetch = FetchType.EAGER)
    private Set<Score> scores;

    public Game(){ }

    public Game(LocalDateTime createdDate){
        this.createdDate = createdDate;
    }

    public long getId(){
        return id;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }


    @JsonIgnore
    public List<Player> getPlayers() {
        return gamePlayers.stream().map(GamePlayer::getPlayer).collect(toList());
    }

    public void addGamePlayer(GamePlayer gamePlayer){
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }

    public Set<GamePlayer> getGamePlayers() { return gamePlayers; }

    public void SetGamePlayers(Set<GamePlayer> gamePlayers){
        this.gamePlayers = gamePlayers;
    }

    public Map<String, Object> gameDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", getId());
        dto.put("created", getCreatedDate());
        dto.put("gamePlayers", gamePlayers.stream()
                .map(GamePlayer::gamePlayerDTO).collect(Collectors.toSet()));
        List<Map<String, Object>> scores = gamePlayers.stream()
                .map(gp -> gp.getScore()).filter(score -> score.isPresent()).map(score -> score.get().scoreDTO())
                .collect(Collectors.toList());
        dto.put("scores", scores);

        return dto;
    }
}
