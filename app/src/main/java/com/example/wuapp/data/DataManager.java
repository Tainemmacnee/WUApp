package com.example.wuapp.data;

import android.os.Build;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import com.example.wuapp.model.Event;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.Team;
import com.example.wuapp.model.User;
import com.example.wuapp.model.UserLoginToken;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This Class manages all data that is downloaded or needs downloading
 */
public class DataManager implements Parcelable {

    public static final String REQUEST_SCHEDULED_GAMES = "request_scheduled_games";
    public static final String REQUEST_RECENT_GAMES = "request_recent_games";

    private boolean downloadingGames = false;
    private boolean downloadingEvents = false;

    private Set<Game> gameSet = new HashSet<>();
    private Set<Event> eventSet = new HashSet<>();
    private Queue<Request> requestQueue;

    private UserLoginToken loginToken;

    //TODO: Remove test constructor
    public DataManager(){
        gameSet.add(new Game.Builder().setHomeTeamName("HOME TEAM NAME!").setAwayTeamName("AWAY TEAM").build());
    }

    protected DataManager(Parcel in) {
        gameSet.addAll(in.createTypedArrayList(Game.CREATOR));
        eventSet.addAll(in.createTypedArrayList(Event.CREATOR));
    }

    private void processQueue(){
        Handler handler = new Handler();
        int delay = 500; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(!requestQueue.isEmpty() && !downloadingGames){
                    for(Request r : requestQueue){
                        processRequest(r);
                    }
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void processRequest(Request r){
        ArrayList<Game> results = new ArrayList<>();

        switch (r.request){
            case DataManager.REQUEST_SCHEDULED_GAMES:
                for(Game g : gameSet){
                    if(g.isUpcoming()){ results.add(g); }
                }
                break;
        }

        r.callback.DataReady(results);
    }

    public static final Creator<DataManager> CREATOR = new Creator<DataManager>() {
        @Override
        public DataManager createFromParcel(Parcel in) {
            return new DataManager(in);
        }

        @Override
        public DataManager[] newArray(int size) {
            return new DataManager[size];
        }
    };

    public void makeRequest(DataReady callback, String request){
        requestQueue.add(new Request(callback, request));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        List<Game> gameList = new ArrayList<>();
        List<Event> eventList = new ArrayList<>();
        gameList.addAll(gameSet);
        eventList.addAll(eventSet);
        parcel.writeTypedList(gameList);
        parcel.writeTypedList(eventList);
    }

    private Document downloadWebPage(String link){
        Document result = null;
        long t1 = System.currentTimeMillis();
        try {
            Connection.Response loadPageResponse = Jsoup.connect(link)
                    .method(Connection.Method.GET)
                    .userAgent(User.USER_AGENT)
                    .cookies(loginToken.getCookies())
                    .execute();
            System.out.println("Time to load " + link + ": " + (System.currentTimeMillis() - t1));
            result = loadPageResponse.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void downloadGames(){
        this.downloadingGames = true;

        CompletableFuture<Void> scheduledGames = CompletableFuture.supplyAsync(() ->
            downloadWebPage(loginToken.getLinks().get(UserLoginToken.LINK_SCHEDULED_GAMES))
        ).thenAccept( r -> gameSet.addAll(WDSParser.parseGames(r)));

        CompletableFuture<Void> gamesWithResult = CompletableFuture.supplyAsync(() ->
                downloadWebPage(loginToken.getLinks().get(UserLoginToken.LINK_GAMES_WITH_RESULTS))
        ).thenAccept( r -> gameSet.addAll(WDSParser.parseGames(r)));

        CompletableFuture<Void> gamesWithoutResult = CompletableFuture.supplyAsync(() ->
                downloadWebPage(loginToken.getLinks().get(UserLoginToken.LINK_GAMES_MISSING_RESULTS))
        ).thenAccept( r -> gameSet.addAll(WDSParser.parseGames(r)));

        CompletableFuture combinedFutures = CompletableFuture.allOf(scheduledGames, gamesWithResult, gamesWithoutResult)
                .thenAccept(r -> this.downloadingGames = false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void downloadEvents() {
        this.downloadingEvents = true;

        CompletableFuture<Void> events = CompletableFuture.supplyAsync(() ->
                downloadWebPage(loginToken.getLinks().get(UserLoginToken.LINK_WEB_DASHBOARD))
        ).thenApply(r -> WDSParser.parseEvents(r)
        ).thenAccept(r -> {
            r.stream().forEach(event -> event.setTeams(downloadEventTeams(event.getStandingsLink())));
            this.eventSet.addAll(r);
            this.downloadingEvents = false;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Set<Team> downloadEventTeams(String eventLink) {

        CompletableFuture<Set<Team>> teamsPage1 = CompletableFuture.supplyAsync(() ->
                downloadWebPage(eventLink+"/teams?page=1")
        ).thenApply(r -> WDSParser.parseTeams(r));

        CompletableFuture<Set<Team>> teamsPage2 = CompletableFuture.supplyAsync(() ->
                downloadWebPage(eventLink+"/teams?page=2")
        ).thenApply(r -> WDSParser.parseTeams(r));

        CompletableFuture<Set<Team>> teamsPage3 = CompletableFuture.supplyAsync(() ->
                downloadWebPage(eventLink+"/teams?page=3")
        ).thenApply(r -> WDSParser.parseTeams(r));

        CompletableFuture combinedFutures = CompletableFuture.allOf(teamsPage1, teamsPage2, teamsPage3)
                .thenApply(r -> Stream.of(teamsPage1, teamsPage2, teamsPage3).flatMap(f -> f.join().stream()).collect(Collectors.toSet()));

        return (Set<Team>) combinedFutures.join();
    }


    private class Request{

        public final String request;
        public final DataReady callback;

        public Request(DataReady callback, String request){
            this.request = request;
            this.callback = callback;
        }

    }

    @Override
    public String toString() {
        return "DataManager{" +
                "gameSet=" + gameSet +
                ", requestQueue=" + requestQueue +
                '}';
    }
}


