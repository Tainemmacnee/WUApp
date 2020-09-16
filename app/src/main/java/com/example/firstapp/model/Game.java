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

    String homeTeamName, homeTeamImg;
    String awayTeamName, awayTeamImg;
    String league, date, time, location;

    public String homeTeamScore, homeTeamSpirit;
    public String awayTeamScore, awayTeamSpirit;
    public String reportLink;

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getHomeTeamImg() {
        return homeTeamImg;
    }

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

    public String getLocation() {
        return location;
    }

    public Game(String homeTeamName, String homeTeamImg, String awayTeamName, String awayTeamImg, String league, String date, String time, String location){
        this.homeTeamName = homeTeamName;
        this.homeTeamImg = homeTeamImg;
        this.awayTeamName = awayTeamName;
        this.awayTeamImg = awayTeamImg;
        this.league = league;
        this.time = time;
        this.date = date;
        this.location = location;
    }
}
