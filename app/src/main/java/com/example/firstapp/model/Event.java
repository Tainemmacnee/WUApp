package com.example.firstapp.model;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Event implements Serializable{

    String eventName, eventImg, standingsLink;
    Map<String, String> cookies;
    Future<List<Team>> teams;

    public Event(String eventName, String eventImg, Future<List<Team>> teams, String standingsLink, Map<String, String> cookies)

    {
        this.teams = teams;
        this.eventName = eventName;
        this.eventImg = eventImg.replace("40", "200");
        this.standingsLink = standingsLink;
        this.cookies = cookies;
    }

    public String getName() {
        return this.eventName;
    }

    public String getEventImg(){
        return this.eventImg;
    }

    public HashMap<String, String> getCookies() {
        return (HashMap<String, String>) cookies;
    }

    public String getStandingsLink() {
        return standingsLink;
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

    public Team getTeam(String teamName){
        for(Team t : getTeams()){
            if(t.getName().equals(teamName)){
                return t;
            }
        }
        return null;
    }
}
