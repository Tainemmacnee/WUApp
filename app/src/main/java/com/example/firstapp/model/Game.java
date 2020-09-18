package com.example.firstapp.model;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Game {

    private String homeTeamName, homeTeamImg, awayTeamName, awayTeamImg, league, date, time,
            location, homeTeamScore, homeTeamSpirit, awayTeamScore, awayTeamSpirit, reportLink;

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getHomeTeamImg() {
        return homeTeamImg;
    }

    public String getHomeTeamScore() { return homeTeamScore; }

    public String getHomeTeamSpirit() { return homeTeamSpirit; }

    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }

    public String getLeague() {
        return this.league;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public String getAwayTeamImg() {
        return awayTeamImg;
    }

    public String getAwayTeamScore() { return awayTeamScore; }

    public String getAwayTeamSpirit() { return awayTeamSpirit; }

    public String getLocation() {
        return location;
    }

    public String getReportLink() { return reportLink; }

    private Game(Builder builder){
        this.homeTeamName = builder.homeTeamName;
        this.homeTeamImg = builder.homeTeamImg;
        this.homeTeamSpirit = builder.homeTeamSpirit;
        this.homeTeamScore = builder.homeTeamScore;
        this.awayTeamName = builder.awayTeamName;
        this.awayTeamImg = builder.awayTeamImg;
        this.awayTeamSpirit = builder.awayTeamSpirit;
        this.awayTeamScore = builder.awayTeamScore;
        this.league = builder.league;
        this.time = builder.time;
        this.date = builder.date;
        this.location = builder.location;
        this.reportLink = builder.reportLink;
    }

//    public Game(String homeTeamName, String homeTeamImg, String awayTeamName, String awayTeamImg, String league, String date, String time, String location){
//        this.homeTeamName = homeTeamName;
//        this.homeTeamImg = homeTeamImg;
//        this.awayTeamName = awayTeamName;
//        this.awayTeamImg = awayTeamImg;
//        this.league = league;
//        this.time = time;
//        this.date = date;
//        this.location = location;
//    }

    public static class Builder {
        private String homeTeamName, homeTeamImg, awayTeamName, awayTeamImg, league, date, time,
                location, homeTeamScore, homeTeamSpirit, awayTeamScore, awayTeamSpirit, reportLink;

        public Builder setHomeTeamName(String homeTeamName){ this.homeTeamName = homeTeamName; return this; }

        public Builder setHomeTeamImg(String homeTeamImg){ this.homeTeamImg = homeTeamImg; return this; }

        public Builder setHomeTeamScore(String homeTeamScore){ this.homeTeamScore = homeTeamScore; return this; }

        public Builder setHomeTeamSpirit(String homeTeamSpirit){ this.homeTeamSpirit = homeTeamSpirit; return this; }

        public Builder setAwayTeamName(String awayTeamName){ this.awayTeamName = awayTeamName; return this; }

        public Builder setAwayTeamImg(String awayTeamImg){ this.awayTeamImg = awayTeamImg; return this; }

        public Builder setAwayTeamScore(String awayTeamScore){ this.awayTeamScore = awayTeamScore; return this; }

        public Builder setAwayTeamSpirit(String awayTeamSpirit){ this.awayTeamSpirit = awayTeamSpirit; return this; }

        public Builder setLeague(String league){ this.league = league; return this; }

        public Builder setDate(String date){ this.date = date; return this; }

        public Builder setTime(String time){ this.time = time; return this; }

        public Builder setLocation(String location){ this.location = location; return this; }

        public Builder setReportLink(String reportLink){ this.reportLink = reportLink; return this; }

        public Game build(){
            return new Game(this);
        }
    }
}
