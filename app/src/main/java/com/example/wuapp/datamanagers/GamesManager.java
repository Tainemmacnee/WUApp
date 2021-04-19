package com.example.wuapp.datamanagers;


import com.example.wuapp.parsers.WDSParser;
import com.example.wuapp.exceptions.InvalidLinkException;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.UserLoginToken;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class GamesManager extends DataManager {

    private static GamesManager gamesManager;

    public static final String REQUEST_SCHEDULED_GAMES = "request_scheduled_games";
    public static final String REQUEST_RECENT_GAMES = "request_recent_games";

    private Set<Game> games = new HashSet<>();

    private GamesManager(UserLoginToken loginToken){
        this.loginToken = loginToken;
        downloadGames();
    }

    public static void initialise(UserLoginToken loginToken){
        if(gamesManager == null){
            gamesManager = new GamesManager(loginToken);
        }
    }

    public static GamesManager getInstance(){ return gamesManager; }


    @Override
    public void requestData(DataReceiver.Request request) {
        this.request = request;
        if(!downloading) { prepareResponse(); }
    }

    @Override
    public void reload() {
        games = new HashSet<>();
        exception = null;
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
        this.downloading = true;
        CompletableFuture scheduledGames = downloadGamesFromLink(loginToken.getLinks().get(UserLoginToken.LINK_SCHEDULED_GAMES));

        CompletableFuture gameWithResult = downloadGamesFromLink(loginToken.getLinks().get(UserLoginToken.LINK_GAMES_WITH_RESULTS));

        CompletableFuture gamesWithoutResult = downloadGamesFromLink(loginToken.getLinks().get(UserLoginToken.LINK_GAMES_MISSING_RESULTS));

        CompletableFuture.allOf(gamesWithoutResult, gameWithResult, scheduledGames)
                .whenComplete((result, ex) -> { this.downloading = false; prepareResponse(); });
    }

    private CompletableFuture downloadGamesFromLink(String link){
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return downloadWebPage(link);
                    } catch (Exception | InvalidLinkException e) { //pass exception on to whenComplete function
                        throw new CompletionException(e);
                    }
                }
        ).thenAcceptAsync( r -> { //parse and store games
            try {
                Set<Game> games = WDSParser.parseGames(r);
                this.games.addAll(games);
            } catch (ParseException e){ //pass exception on to whenComplete function
                throw new CompletionException(e);
            }
        }).whenComplete( //catches any exceptions that occur/were passed on
                (msg, ex) -> { exception = ex.getCause(); }
        );
        return future;
    }
}
