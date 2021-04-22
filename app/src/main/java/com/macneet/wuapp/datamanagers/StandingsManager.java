package com.macneet.wuapp.datamanagers;

import com.macneet.wuapp.parsers.WDSParser;
import com.macneet.wuapp.exceptions.ElementNotFoundException;
import com.macneet.wuapp.exceptions.InvalidLinkException;
import com.macneet.wuapp.model.UserLoginToken;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class StandingsManager extends DataManager {

    public static final String REQUEST_STANDINGS = "request_standings";

    private static StandingsManager standingsManager;
    private List<Map<String, String>> standings;

    private StandingsManager(UserLoginToken loginToken){
        this.loginToken = loginToken;
    }

    public static void initialise(UserLoginToken loginToken){
        if(standingsManager == null){
            standingsManager = new StandingsManager(loginToken);
        }
    }

    public static StandingsManager getInstance(){ return standingsManager; }

    private void prepareResponse(){
        submitResponse(new DataReceiver.Response(standings, exception, request));
    }

    @Override
    public void requestData(DataReceiver.Request request) {
        this.request = request;
        downloadStandings(request.link);
    }

    @Override
    public void reload() { exception = null; }

    public void downloadStandings(String link){
        this.downloading = true;
        CompletableFuture.supplyAsync(() -> {
            try {
                return downloadWebPage(link);
            } catch (Exception | InvalidLinkException e) {
                throw new CompletionException(e);
            }
        }).thenApply(r -> {
                    try {
                        return WDSParser.parseStandings(r);
                    } catch (ParseException e) {
                        throw new CompletionException(e);
                    } catch (ElementNotFoundException e){
                        throw new CompletionException(e);
                    }
                }
        ).thenAccept(r -> {
            standings = r;
            this.downloading = false;
        }).whenComplete((msg, ex) -> {
            if(ex != null) {
                exception = ex.getCause();
            }
            downloading = false;
            prepareResponse();
        });
    }
}
