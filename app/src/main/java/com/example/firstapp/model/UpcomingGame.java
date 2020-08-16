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

public class UpcomingGame {

    String homeTeamName, homeTeamImg;
    String awayTeamName, awayTeamImg;
    String league, date, time, location;

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

    public UpcomingGame(String homeTeamName, String homeTeamImg, String awayTeamName, String awayTeamImg, String league, String date, String time, String location){
        this.homeTeamName = homeTeamName;
        this.homeTeamImg = homeTeamImg;
        this.awayTeamName = awayTeamName;
        this.awayTeamImg = awayTeamImg;
        this.league = league;
        this.time = time;
        this.date = date;
        this.location = location;
    }

    public static Future<List<UpcomingGame>> LoadUpcomingGames(Map<String, String> cookies, String link){
        ExecutorService executor = Executors.newCachedThreadPool();
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        final String WEB_URL = "https://wds.usetopscore.com"+link;
        List<UpcomingGame> output = new ArrayList<>();

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

                    output.add(new UpcomingGame(homeTeamName, homeTeamImg, awayTeamName, awayTeamImg, league, date, time, location));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return output;
        });
    }


}
