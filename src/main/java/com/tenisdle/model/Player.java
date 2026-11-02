package com.tenisdle.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jugador tal y como lo devuelve /v1/search_players.
 * Los campos de la API vienen en snake_case, por eso se usa @JsonProperty.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {

    @JsonProperty("player_id")
    private String playerId;

    private String name;

    // Codigo de pais de tres letras (ESP, ITA...)
    private String country;

    // Formato yyyy-MM-dd
    @JsonProperty("date_of_birth")
    private String dateOfBirth;

    private int matches;

    @JsonProperty("last_match")
    private String lastMatch;

    // Puede venir a null si el jugador no tiene ranking
    @JsonProperty("current_rank")
    private Integer currentRank;

    // "right" o "left"
    private String plays;

    public Player() {
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public String getLastMatch() {
        return lastMatch;
    }

    public void setLastMatch(String lastMatch) {
        this.lastMatch = lastMatch;
    }

    public Integer getCurrentRank() {
        return currentRank;
    }

    public void setCurrentRank(Integer currentRank) {
        this.currentRank = currentRank;
    }

    public String getPlays() {
        return plays;
    }

    public void setPlays(String plays) {
        this.plays = plays;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerId='" + playerId + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", currentRank=" + currentRank +
                ", plays='" + plays + '\'' +
                '}';
    }
}
