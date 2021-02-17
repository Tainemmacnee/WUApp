package com.example.wuapp.data;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.wuapp.model.Event;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.ReportFormState;
import com.example.wuapp.model.Team;
import com.example.wuapp.model.User;
import com.example.wuapp.model.UserLoginToken;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This Class manages all data that is downloaded or needs downloading
 */
public class DataManager implements Parcelable {

    public static final String REQUEST_SCHEDULED_GAMES = "request_scheduled_games";
    public static final String REQUEST_RECENT_GAMES = "request_recent_games";
    public static final String REQUEST_EVENTS = "request_events";
    public static final String REQUEST_REPORT_FORM = "request_form";

    public static final String HOME_URL = "https://wds.usetopscore.com";

    private boolean downloadingGames = false;
    private boolean downloadingEvents = false;
    private boolean downloadingReportForm = false;

    private Set<Game> gameSet = new HashSet<>();
    private Set<Event> eventSet = new HashSet<>();
    private Queue<Request> requestQueue = new ArrayDeque<>();
    private ReportFormState currentReportForm;

    private UserLoginToken loginToken;
    private String OAuthToken;

    private ExecutorService executor = Executors.newCachedThreadPool();

    //TODO: Remove test constructor
    @SuppressLint("NewApi")
    public DataManager(UserLoginToken loginToken){
        this.loginToken = loginToken;

        downloadGames();
        downloadEvents();
        downloadOAuthKey();

        processQueue();
    }

    protected DataManager(Parcel in) {
        gameSet.addAll(in.createTypedArrayList(Game.CREATOR));
        eventSet.addAll(in.createTypedArrayList(Event.CREATOR));
        OAuthToken = in.readString();
        loginToken = (UserLoginToken) in.readSerializable();

        processQueue();
    }

    private void processQueue(){
        Handler handler = new Handler();
        int delay = 500; //milliseconds

        handler.postDelayed(new Runnable(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run(){
                //do something
                if(!requestQueue.isEmpty()){
                    processRequest(requestQueue.poll());
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void processRequest(Request r){
        ArrayList results = new ArrayList<>();

        switch (r.request){
            case DataManager.REQUEST_SCHEDULED_GAMES:
                if(downloadingGames) { requestQueue.add(r); break; }
                for(Game g : gameSet){
                    if(g.isUpcoming()){ results.add(g); }
                }
                break;
            case DataManager.REQUEST_RECENT_GAMES:
                if(downloadingGames) {  requestQueue.add(r); break; }
                for(Game g : gameSet){
                    if(!g.isUpcoming()){ results.add(g); }
                }
                break;

            case DataManager.REQUEST_EVENTS:
                if(downloadingEvents) {  requestQueue.add(r); break; }
                results.addAll(eventSet);
                r.callback.receiveData(results);
                return;

            case DataManager.REQUEST_REPORT_FORM:
                if(downloadingReportForm) {  requestQueue.add(r); break; }
                results.add(currentReportForm);
                r.callback.receiveData(results);
        }

        r.callback.receiveData(results);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void makeRequest(DataReceiver callback, String request, String link){
        requestQueue.add(new Request(callback, request));
        downloadReportForm(link);
    }

    public void makeRequest(DataReceiver callback, String request){
        requestQueue.add(new Request(callback, request));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void downloadReportForm(String link){
        this.downloadingReportForm = true;
        executor.submit(() -> {
            CompletableFuture.supplyAsync(() ->
                    downloadWebPage(link)
            ).thenApply(r -> WDSParser.parseReportForm(r)
            ).thenAccept(r -> {
                currentReportForm = r;
                this.downloadingReportForm = false;
            }).join();
        });
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
        parcel.writeString(OAuthToken);
        parcel.writeSerializable(loginToken);
    }

    private Document downloadWebPage(String link){
        System.out.println(link);
        if(link == null) {return null;}
        Document result = null;
        try {
            Connection.Response loadPageResponse = Jsoup.connect(link)
                    .method(Connection.Method.GET)
                    .userAgent(User.USER_AGENT)
                    .cookies(loginToken.getCookies())
                    .execute();
            result = loadPageResponse.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void downloadGames(){
        this.downloadingGames = true;
        executor.submit(() -> {
            CompletableFuture<Void> scheduledGames = CompletableFuture.supplyAsync(() ->
                    downloadWebPage(loginToken.getLinks().get(UserLoginToken.LINK_SCHEDULED_GAMES))
            ).thenAccept( r -> gameSet.addAll(WDSParser.parseGames(r)));

            CompletableFuture<Void> gamesWithResult = CompletableFuture.supplyAsync(() ->
                    downloadWebPage(loginToken.getLinks().get(UserLoginToken.LINK_GAMES_WITH_RESULTS))
            ).thenAccept( r -> gameSet.addAll(WDSParser.parseGames(r)));

            CompletableFuture<Void> gamesWithoutResult = CompletableFuture.supplyAsync(() ->
                    downloadWebPage(loginToken.getLinks().get(UserLoginToken.LINK_GAMES_MISSING_RESULTS))
            ).thenAccept( r -> gameSet.addAll(WDSParser.parseGames(r)));

            CompletableFuture combinedFutures = CompletableFuture.allOf(gamesWithoutResult, gamesWithResult, scheduledGames)
                    .thenAccept(r -> this.downloadingGames = false);

            combinedFutures.join();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void downloadEvents() {
        this.downloadingEvents = true;
            executor.submit(() -> {
            CompletableFuture.supplyAsync(() ->
                    downloadWebPage(HOME_URL)
            ).thenApply(r -> WDSParser.parseEvents(r)
            ).thenAccept(r -> {
                r.stream().forEach(event -> event.setTeams(downloadEventTeams(event.getStandingsLink())));
                this.eventSet.addAll(r);
                this.downloadingEvents = false;
            }).join();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void downloadOAuthKey() {

        final String WEB_URL = "https://wds.usetopscore.com/u/oauth-key";

        executor.submit(() -> {

            CompletableFuture.supplyAsync(() -> {

                List<String> output = new ArrayList<>();

                Document doc = downloadWebPage(WEB_URL);
                Element table = doc.getElementsByClass("table no-border").first();

                for (Element row : table.getElementsByTag("tr")) { //find id and secret
                    output.add(row.getElementsByTag("td").first().text());
                }

                output.remove(2); //remove useless string

                return output;

            }).thenApply(r -> {
                Document response = null;
                try {
                    response = Jsoup.connect("https://wds.usetopscore.com/api/oauth/server")
                            .userAgent(User.USER_AGENT)
                            .data("grant_type", "client_credentials")
                            .data("client_id", r.get(0))
                            .data("client_secret", r.get(1))
                            .ignoreContentType(true)
                            .post();

                    JSONObject result = new JSONObject(response.body().text());

                    return result.get("access_token");

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }).thenAccept(r -> OAuthToken = (String) r
            ).join();
        });
    }

    public Map<String, String> getCookies() {
        return loginToken.getCookies();
    }

    public String getOAuthToken() {
        return this.OAuthToken;
    }


    private class Request{

        public final String request;
        public final DataReceiver callback;

        public Request(DataReceiver callback, String request){
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


