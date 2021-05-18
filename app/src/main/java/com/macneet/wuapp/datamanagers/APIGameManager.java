package com.macneet.wuapp.datamanagers;

import com.macneet.wuapp.exceptions.InvalidLinkException;
import com.macneet.wuapp.model.Game;
import com.macneet.wuapp.model.UserLoginToken;
import com.macneet.wuapp.parsers.APIParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class APIGameManager extends DataManager {

    private static APIGameManager gamesManager;

    public static final String REQUEST_SCHEDULED_GAMES = "request_scheduled_games";
    public static final String REQUEST_RECENT_GAMES = "request_recent_games";

    private Set<Game> games = new HashSet<>();

    private APIGameManager(UserLoginToken loginToken){
        this.loginToken = loginToken;
        downloadGames();
    }

    public static void initialise(UserLoginToken loginToken){
        if(gamesManager == null){
            gamesManager = new APIGameManager(loginToken);
        }
    }

    public static APIGameManager getInstance(){ return gamesManager; }

    @Override
    public void requestData(DataReceiver.Request request) {
        this.request = request;
        if(!downloading) { prepareResponse(); }
    }

    @Override
    public void reload() {
        games.clear();
        downloadGames();
    }

    public void prepareResponse(){
        if(request == null) { return; }
        ArrayList<Game> gameData = new ArrayList<>();
        switch (request.request){
            case REQUEST_RECENT_GAMES:
                for(Game g : games){
                    if(!g.isUpcoming()){ gameData.add(g); }
                }
                Collections.sort(gameData, new Game.SortByLeastRecentDate());
                break;
            case REQUEST_SCHEDULED_GAMES:
                for(Game g : games){
                    if(g.isUpcoming()){ gameData.add(g); }
                }
                Collections.sort(gameData, new Game.SortByMostRecentDate());
                break;
        }
        submitResponse(new DataReceiver.Response<>(gameData, exception, request));
    }

    private void downloadGames(){
        downloading = true; exception = null;
        CompletableFuture.supplyAsync(() -> {
                try {
                    return Jsoup.connect("https://wds.usetopscore.com/api/games?active_events_only=true&person_id="+ loginToken.getPersonID())
                            .method(Connection.Method.GET)
                            .userAgent(USER_AGENT)
                            .ignoreContentType(true)
                            .cookies(loginToken.getCookies())
                            .execute();
                } catch (Exception e) { //pass exception on to whenComplete function
                    throw new CompletionException(e);
                }
        })
                .thenApply(r -> {
                    try {
                        return new JSONObject(r.body()).getJSONArray("result");
                    } catch (JSONException e) {
                        throw new CompletionException(e);
                    }
        })
                .thenApplyAsync(r -> APIParser.parseGames(r))
                .thenAccept(r -> games.addAll(r))
                .whenComplete((msg, ex) -> {exception = ex; downloading = false; prepareResponse();});
    }
}
