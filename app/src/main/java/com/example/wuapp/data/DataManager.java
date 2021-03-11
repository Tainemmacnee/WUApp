package com.example.wuapp.data;

import android.content.Context;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    public static final String REQUEST_EVENTS = "request_events";
    public static final String REQUEST_REPORT_FORM = "request_form";
    public static final String REQUEST_STANDINGS = "request_standings";

    public static final String HOME_URL = "https://wds.usetopscore.com";

    private boolean downloadingGames = false;
    private boolean downloadingEvents = false;
    private boolean downloadingReportForm = false;
    private boolean downloadingStandings = false;

    private Context context;
    private Config config;
    private Date eventCacheTimestamp;

    private Set<Game> gameSet = new HashSet<>();
    private Set<Event> eventSet = new HashSet<>();
    private Queue<Request> requestQueue = new ArrayDeque<>();
    private ReportFormState currentReportForm;
    private List<Map<String, String>> currentStandings;

    private UserLoginToken loginToken;
    private String OAuthToken;

    public DataManager(UserLoginToken loginToken, Context context){
        this.loginToken = loginToken;
        this.config = Config.readConfig(context);
        this.context = context; //used to save events

        downloadGames();
        loadEvents();
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
        int delay = 200; //milliseconds

        handler.postDelayed(new Runnable(){
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
                if(downloadingGames) { requestQueue.add(r); return; }
                for(Game g : gameSet){
                    if(g.isUpcoming()){ results.add(g); }
                }
                break;
            case DataManager.REQUEST_RECENT_GAMES:
                if(downloadingGames) {  requestQueue.add(r); return; }
                for(Game g : gameSet){
                    if(!g.isUpcoming()){ results.add(g); }
                }
                break;

            case DataManager.REQUEST_EVENTS:
                if(downloadingEvents) {  requestQueue.add(r); return; }
                results.addAll(eventSet);
                r.callback.receiveData(results);
                return;

            case DataManager.REQUEST_REPORT_FORM:
                if(downloadingReportForm) {  requestQueue.add(r); break; }
                results.add(currentReportForm);
                r.callback.receiveData(results);
                return;

            case DataManager.REQUEST_STANDINGS:
                if(downloadingStandings) { requestQueue.add(r); break; }
                results.add(currentStandings);
                r.callback.receiveData(results);
                return;
        }

        r.callback.receiveData(results);
    }

    public void setCacheEvents(boolean bool){
        this.config.setCacheEvents(bool, context);
        if(bool == false){
            deleteCachedEvents();
        }
    }

    private void deleteCachedEvents(){
        File file = new File(context.getFilesDir(), "events.txt");
        if(file.exists()){
            file.delete();
        }
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

    public void makeRequest(DataReceiver callback, String request, String link){
        switch (request){
            case DataManager.REQUEST_REPORT_FORM:
                downloadReportForm(link);
                break;
            case DataManager.REQUEST_STANDINGS:
                downloadStandings(link);
                break;
        }
        requestQueue.add(new Request(callback, request));
    }

    public void makeRequest(DataReceiver callback, String request){
        requestQueue.add(new Request(callback, request));
    }

    public void downloadReportForm(String link){
        this.downloadingReportForm = true;
            CompletableFuture.supplyAsync(() ->
                    downloadWebPage(link)
            ).thenApply(WDSParser::parseReportForm
            ).thenAccept(r -> {
                currentReportForm = r;
                this.downloadingReportForm = false;
            });
    }

    public void downloadStandings(String link){
        this.downloadingStandings = true;
           CompletableFuture.supplyAsync(() ->
               downloadWebPage(link)
           ).thenApply(WDSParser::parseStandings
           ).thenAccept(r -> {
                currentStandings = r;
                this.downloadingStandings = false;
            });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        List<Game> gameList = new ArrayList<>(gameSet);
        List<Event> eventList = new ArrayList<>(eventSet);
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

    /**
     * This function checks that if the events are already downloaded, then
     * for all the games in the given set the games event exists
     */
    private boolean checkEventIsCached(Game game){
        //if we are downloading the events, it is safe to assume we will download the event for the game.
        if(downloadingEvents) { return true; }

        for(Event event : eventSet){
            if(event.getName().equals(game.getLeague())){
                return true;
            }
        }
        return false;
    }

    private void downloadGames(){
        this.downloadingGames = true;

            CompletableFuture<Void> scheduledGames = CompletableFuture.supplyAsync(() ->
                    downloadWebPage(loginToken.getLinks().get(UserLoginToken.LINK_SCHEDULED_GAMES))
            ).thenApply( r -> {
                Set<Game> games = WDSParser.parseGames(r);
                if(games.stream().noneMatch(g -> checkEventIsCached(g))){ downloadEvents(); }
                return games;
            }).thenAccept( r -> gameSet.addAll(r));

            CompletableFuture<Void> gameWithResult = CompletableFuture.supplyAsync(() ->
                    downloadWebPage(loginToken.getLinks().get(UserLoginToken.LINK_GAMES_WITH_RESULTS))
            ).thenApply( r -> {
                Set<Game> games = WDSParser.parseGames(r);
                if(games.stream().noneMatch(g -> checkEventIsCached(g))){ downloadEvents(); }
                return games;
            }).thenAccept( r -> gameSet.addAll(r));

            CompletableFuture<Void> gamesWithoutResult = CompletableFuture.supplyAsync(() ->
                    downloadWebPage(loginToken.getLinks().get(UserLoginToken.LINK_GAMES_MISSING_RESULTS))
            ).thenApply( r -> {
                Set<Game> games = WDSParser.parseGames(r);
                if(games.stream().noneMatch(g -> checkEventIsCached(g))){ downloadEvents(); }
                return games;
            }).thenAccept( r -> gameSet.addAll(r));

            CompletableFuture combinedFutures = CompletableFuture.allOf(gamesWithoutResult, gameWithResult, scheduledGames)
                    .thenAccept(r -> this.downloadingGames = false);
    }

    private void loadEvents(){
        File file = new File(context.getFilesDir(), "events.txt");
        if(file.exists()) {
            readEvents();
        } else {
            downloadEvents();
        }
    }

    private void downloadEvents() {
        this.downloadingEvents = true;

            CompletableFuture.supplyAsync(() -> downloadWebPage(HOME_URL)
            ).thenApply(r -> WDSParser.parseEvents(r)
            ).thenApply(r -> {
                r.parallelStream().forEach(event -> event.setTeams(downloadEventTeams(event.getEventLink())));
                this.eventSet.addAll(r);
                this.downloadingEvents = false;
                return r;
            }).thenAccept(r -> {
                        if(config.getCacheEvents()) { writeEvents(r); eventCacheTimestamp = new Date(); }
                    }
            );

    }

    private void writeEvents(Set<Event> events){
        deleteCachedEvents(); //Delete old events in preparation for new events to be saved
        try (FileOutputStream fout = context.openFileOutput("events.txt", Context.MODE_PRIVATE); ObjectOutputStream oos = new ObjectOutputStream(fout)) {
                oos.writeObject(eventCacheTimestamp);
                oos.writeObject(events);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readEvents(){
        FileInputStream fin = null;
        ObjectInputStream oin = null;
        try {
            fin = context.openFileInput("events.txt");
            if(fin != null){
                oin = new ObjectInputStream(fin);
                eventCacheTimestamp = (Date) oin.readObject();
                eventSet = (Set<Event>) oin.readObject();
            }
        }catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try { if (fin != null) fin.close(); } catch(IOException ignored) {}
            try { if (oin != null) oin.close(); } catch(IOException ignored) {}
        }
    }

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

    private void downloadOAuthKey() {

        final String WEB_URL = "https://wds.usetopscore.com/u/oauth-key";


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
            }).thenAccept(r -> OAuthToken = (String) r);
    }

    public Map<String, String> getCookies() {
        return loginToken.getCookies();
    }

    public String getOAuthToken() {
        return this.OAuthToken;
    }

    public UserLoginToken getLoginToken() {
        return this.loginToken;
    }

    public Config getConfig() {
        return this.config;
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