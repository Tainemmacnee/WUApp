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

    public static Future<List<Game>> LoadMissingResultsGames(Map<String, String> cookies, String link) {
        ExecutorService executor = Executors.newCachedThreadPool();
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        final String WEB_URL = "https://wds.usetopscore.com"+link;
        List<Game> output = new ArrayList<>();

        return executor.submit(() -> {
            System.out.println("MISSING RESULTS LOADING");
            try {
                Connection.Response loadPageResponse = Jsoup.connect(WEB_URL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .cookies(cookies)
                        .execute();

                Document doc = loadPageResponse.parse();
                String homeTeamName, homeTeamImg, awayTeamName, awayTeamImg, league, date, time, location;
                String homeTeamScore, homeTeamSpirit, awayTeamScore, awayTeamSpirit;
                Elements UpcomingGameLinks = doc.getElementsByClass("striped-block");
                for(Element e: UpcomingGameLinks){
                    //Collect game data from page

                    Element scoreBoxDiv = e.getElementsByClass("schedule-score-box").first();
                    Element reportLink = scoreBoxDiv.getElementsByTag("a").first();

                    if(reportLink == null){
                        System.out.println("SKIP");
                        continue;
                    }

                    Element datetimeElem = e.getElementsByClass("clearfix").first();
                    String[] datetime = datetimeElem.text().trim().split(" ");

                    date = datetime[0] + " " + datetime[1];
                    time = datetime[2] + " " + datetime[3];

                    Element locationLeagueElem = e.getElementsByClass("clearfix").last();
                    Element locationElem = locationLeagueElem.getElementsByClass("push-left").first();
                    location = locationElem.text().trim();

                    Element LeagueElem = locationLeagueElem.getElementsByClass("push-right").first();
                    league = LeagueElem.text().trim();

                    Element homeTeamElem = e.getElementsByClass("game-participant").first();
                    Element homeTeamNameElem = homeTeamElem.getElementsByClass("schedule-team-name ").first();
                    homeTeamName = homeTeamNameElem.text().trim();

                    System.out.println("MISSING RESULTS LOADING HOME");
                    Element homeTeamScoreElem = homeTeamElem.getElementsByClass("score ").first();
                    if(homeTeamScoreElem == null) {homeTeamScore = "?";} else{
                        homeTeamScore = homeTeamScoreElem.text().trim();
                    }

                    Element homeTeamSpiritElem = homeTeamElem.getElementsByClass("schedule-score-box-game-result").first();
                    if(homeTeamSpiritElem == null) {homeTeamSpirit = "?";} else {
                        homeTeamSpirit = homeTeamSpiritElem.text().trim();
                    }

                    Element homeTeamImgElem = homeTeamElem.child(0);
                    homeTeamImg = homeTeamImgElem.attr("src");



                    Element awayTeamElem = e.getElementsByClass("game-participant").last();
                    Element awayTeamNameElem = awayTeamElem.getElementsByClass("schedule-team-name ").first();
                    awayTeamName = awayTeamNameElem.text().trim();

                    Element awayTeamScoreElem = awayTeamElem.getElementsByClass("score ").first();
                    if(awayTeamScoreElem == null) {awayTeamScore = "?";} else{
                        awayTeamScore = awayTeamScoreElem.text().trim();
                    }

                    Element awayTeamSpiritElem = awayTeamElem.getElementsByClass("schedule-score-box-game-result").first();
                    if(awayTeamSpiritElem == null) {awayTeamSpirit = "?";} else {
                        awayTeamSpirit = awayTeamSpiritElem.text().trim();
                    }

//                    awayTeamScore = awayTeamElem.getElementsByClass("score ").first().text().trim();
//                    awayTeamSpirit = awayTeamElem.getElementsByClass("schedule-score-box-game-result").first().text().trim();

                    Element awayTeamImgElem = awayTeamElem.child(0);
                    awayTeamImg = awayTeamImgElem.attr("src");

                    Game game = new Game(homeTeamName, homeTeamImg, awayTeamName, awayTeamImg, league, date, time, location);
                    game.homeTeamScore = homeTeamScore;
                    game.awayTeamScore = awayTeamScore;
                    game.homeTeamSpirit = homeTeamSpirit;
                    game.awayTeamSpirit = awayTeamSpirit;
                    System.out.println("MISSING RESULTS GAME ADDED");
                    output.add(game);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return output;
        });
    }

    public static Future<List<Game>> LoadUpcomingGames(Map<String, String> cookies, String link){
        ExecutorService executor = Executors.newCachedThreadPool();
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        final String WEB_URL = "https://wds.usetopscore.com"+link;
        List<Game> output = new ArrayList<>();

        return executor.submit(() -> {
            try {
                Connection.Response loadPageResponse = Jsoup.connect(WEB_URL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .cookies(cookies)
                        .execute();

                Document doc = loadPageResponse.parse();
                String homeTeamName, homeTeamImg, awayTeamName, awayTeamImg, league, date, time, location;
                Elements UpcomingGameLinks = doc.getElementsByClass("striped-block");
                for(Element e: UpcomingGameLinks){
                    //Collect game data from page

                    Element datetimeElem = e.getElementsByClass("clearfix").first();
                    String[] datetime = datetimeElem.text().trim().split(" ");

                    date = datetime[0] + " " + datetime[1];
                    time = datetime[2] + " " + datetime[3];

                    Element locationLeagueElem = e.getElementsByClass("clearfix").last();
                    Element locationElem = locationLeagueElem.getElementsByClass("push-left").first();
                    location = locationElem.text().trim();

                    Element LeagueElem = locationLeagueElem.getElementsByClass("push-right").first();
                    league = LeagueElem.text().trim();

                    Element homeTeamElem = e.getElementsByClass("game-participant").first();
                    homeTeamName = homeTeamElem.text().trim();

                    Element homeTeamImgElem = homeTeamElem.child(0);
                    homeTeamImg = homeTeamImgElem.attr("src");

                    Element awayTeamElem = e.getElementsByClass("game-participant").last();
                    awayTeamName = awayTeamElem.text().trim();

                    Element awayTeamImgElem = awayTeamElem.child(0);
                    awayTeamImg = awayTeamImgElem.attr("src");

                    output.add(new Game(homeTeamName, homeTeamImg, awayTeamName, awayTeamImg, league, date, time, location));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return output;
        });
    }


}
