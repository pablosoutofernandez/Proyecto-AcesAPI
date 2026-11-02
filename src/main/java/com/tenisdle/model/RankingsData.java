package com.tenisdle.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Contenido de "data" en la respuesta de /v1/get_rankings.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RankingsData {

    private String tour;
    private int season;
    private int week;

    @JsonProperty("ranking_date")
    private String rankingDate;

    private List<Ranking> rankings = new ArrayList<>();
    private int count;

    public RankingsData() {
    }

    public String getTour() {
        return tour;
    }

    public void setTour(String tour) {
        this.tour = tour;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getRankingDate() {
        return rankingDate;
    }

    public void setRankingDate(String rankingDate) {
        this.rankingDate = rankingDate;
    }

    public List<Ranking> getRankings() {
        return rankings;
    }

    public void setRankings(List<Ranking> rankings) {
        this.rankings = rankings;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
