package com.example.firstapp.model;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Event implements Serializable{

    String eventName, eventImg;
    Future<List<Team>> teams;

    public Event(String eventName, String eventImg, Future<List<Team>> teams)

    {
        this.teams = teams;
        this.eventName = eventName;
        this.eventImg = eventImg.replace("40", "200");
    }

    public static Future<List<Event>> LoadEvents(Map<String, String> cookies){
        ExecutorService executor = Executors.newCachedThreadPool();
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        final String WEB_URL = "https://wds.usetopscore.com";
        List<Event> output = new ArrayList<>();

        return executor.submit(() -> {
            try {
                Connection.Response loadPageResponse = Jsoup.connect(WEB_URL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .cookies(cookies)
                        .execute();

                Document doc = loadPageResponse.parse();
                String eventName, eventImg;
                Future<List<Team>> eventTeams;

                Elements eventLinks = doc.getElementsByClass("global-toolbar-subnav-img-item plain-link");
                for(Element e: eventLinks){
                    if(e.attr("href").startsWith("/e/")) {
                        eventTeams = Team.loadTeams(e.attr("href"), cookies);
                    } else {
                        continue;
                    }

                    //Collect event image url from page
                    Element eventImgElem = e.child(0);
                    eventImg = eventImgElem.attr("src");

                    //Collect event name from page
                    Element eventNameElem = e.child(1);
                    eventName = eventNameElem.text();

                    output.add(new Event(eventName, eventImg, eventTeams));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return output;
        });
    }

    public String getName() {
        return this.eventName;
    }

    public String getEventImg(){
        return this.eventImg;
    }

    public List<Team> getTeams(){
        List<Team> teams = null;
        try {
            teams =  this.teams.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return teams;
    }
}
