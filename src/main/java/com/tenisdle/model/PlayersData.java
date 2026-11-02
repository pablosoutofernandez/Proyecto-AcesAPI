package com.tenisdle.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Contenido de "data" en la respuesta de /v1/search_players.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayersData {

    private String tour;
    private List<Player> players = new ArrayList<>();
    private int count;
    private String note;

    public PlayersData() {
    }

    public String getTour() {
        return tour;
    }

    public void setTour(String tour) {
        this.tour = tour;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
