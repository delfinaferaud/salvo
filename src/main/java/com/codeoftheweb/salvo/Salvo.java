package com.codeoftheweb.salvo;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class Salvo {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private int turn;

    @ElementCollection
    @Column (name = "salvoLocation")
    private List<String> salvoLocations;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn (name = "gamePlayer_Id")
    private GamePlayer gamePlayer;

    public Salvo(){
       this.salvoLocations = new ArrayList<>();
    }

    public Salvo (int turn, GamePlayer gamePlayer, List<String> salvoLocation){
        this.turn = turn;
        this.gamePlayer = gamePlayer;
        this.salvoLocations = salvoLocation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public GamePlayer getGamePlayers() {
        return gamePlayer;
    }

    public void setGamePlayers(GamePlayer gamePlayers) {
        this.gamePlayer = gamePlayers;
    }

    public Map<String, Object> SalvoDTO() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("turn", this.turn);
        dto.put("player", getGamePlayers().getPlayer().getId());
        dto.put("locations", this.salvoLocations);

        return dto;

    }

}
