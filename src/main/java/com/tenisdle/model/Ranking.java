package com.tenisdle.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Una fila del ranking devuelto por /v1/get_rankings.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ranking {

    private int rank;

    // Puede venir a null si el jugador acaba de entrar en el ranking
    @JsonProperty("previous_rank")
    private Integer previousRank;

    private int points;

    @JsonProperty("player_id")
    private String playerId;

    private String name;
    private String country;

    public Ranking() {
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Integer getPreviousRank() {
        return previousRank;
    }

    public void setPreviousRank(Integer previousRank) {
        this.previousRank = previousRank;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Ranking{" +
                "rank=" + rank +
                ", points=" + points +
                ", playerId='" + playerId + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
