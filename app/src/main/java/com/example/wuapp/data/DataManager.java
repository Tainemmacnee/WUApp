package com.example.wuapp.data;

import android.content.Context;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.wuapp.exceptions.ElementNotFoundException;
import com.example.wuapp.exceptions.InvalidLinkException;
import com.example.wuapp.model.Event;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.ReportFormState;
import com.example.wuapp.model.Team;
import com.example.wuapp.model.UserLoginToken;
import com.example.wuapp.data.DataReceiver.*;

import org.json.JSONException;
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
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";

    private boolean downloadingGames = false;
    private boolean downloadingEvents = false;
    private boolean downloadingReportForm = false;
    private boolean downloadingStandings = false;

    private Throwable gameLoadException;
    private Throwable eventLoadException;
    private Throwable standingsLoadException;
    private Throwable reportResultLoadException;

    private Context context;
    private Config config;

    private Set<Game> gameSet = new HashSet<>();
    private Set<Event> eventSet = new HashSet<>();
    private Queue<DataReceiver.Request> requestQueue = new ArrayDeque<>();
    private Request currentRequest;
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

    public void addRequest(Request request){
        switch (request.request){
            case DataManager.REQUEST_REPORT_FORM:
                downloadReportForm(request.link);
                break;
            case DataManager.REQUEST_STANDINGS:
                downloadStandings(request.link);
                break;
            case DataManager.REQUEST_EVENTS:
                if(eventSet.isEmpty() && !downloadingEvents){
                    downloadEvents();
                }
            case DataManager.REQUEST_SCHEDULED_GAMES:
                if(gameSet.isEmpty() && !downloadingGames){
                    downloadGames();
                }
            case DataManager.REQUEST_RECENT_GAMES:
                if(gameSet.isEmpty() && !downloadingGames){
                    downloadGames();
                }
        }
        currentRequest = request;
        processQueue();
    }

    private void processQueue(){
        Handler handler = new Handler();
        int delay = 200; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(currentRequest != null){
                    if(!processRequest(currentRequest)){
                        handler.postDelayed(this, delay);
                    }
                }
            }
        }, delay);
    }

    private boolean processRequest(Request r){
        ArrayList results = new ArrayList<>();

        switch (r.request){
            case DataManager.REQUEST_SCHEDULED_GAMES:
                if(downloadingGames) { return false; }
                for(Game g : gameSet){
                    if(g.isUpcoming()){ results.add(g); }
                }
                r.callback.receiveResponse(new Response<Game>(results, gameLoadException));
                break;

            case DataManager.REQUEST_RECENT_GAMES:
                if(downloadingGames) {  return false; }
                for(Game g : gameSet){
                    if(!g.isUpcoming()){ results.add(g); }
                }
                r.callback.receiveResponse(new Response<Game>(results, gameLoadException));
                break;

            case DataManager.REQUEST_EVENTS:
                if(downloadingEvents) {  return false; }
                results.addAll(eventSet);
                r.callback.receiveResponse(new Response<Game>(results, eventLoadException));
                break;

            case DataManager.REQUEST_REPORT_FORM:
                if(downloadingReportForm) {  return false; }
                results.add(currentReportForm);
                r.callback.receiveResponse(new Response<Game>(results, reportResultLoadException));
                break;

            case DataManager.REQUEST_STANDINGS:
                if(downloadingStandings) { return false; }
                r.callback.receiveResponse(new Response<Map<String, String>>(currentStandings, standingsLoadException));
                break;
        }
        currentRequest = null;
        return true;
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

    private Document downloadWebPage(String link) throws IOException, InvalidLinkException {
        System.out.println(link);
        if(link == null) { throw new InvalidLinkException(); }
            return Jsoup.connect(link)
                .method(Connection.Method.GET)
                .userAgent(USER_AGENT)
                .cookies(loginToken.getCookies())
                .execute()
                .parse();
    }

    public void downloadReportForm(String link){
        this.downloadingReportForm = true;
            CompletableFuture.supplyAsync(() -> {
                try {
                    return downloadWebPage(link);
                } catch (Exception | InvalidLinkException e) {
                    e.printStackTrace();
                    return null;
                }
            }).thenApply(WDSParser::parseReportForm
            ).thenAccept(r -> {
                        currentReportForm = r;
                        this.downloadingReportForm = false;
            }).whenComplete((msg, ex) -> reportResultLoadException = ex);
    }

    public void downloadStandings(String link){
        this.downloadingStandings = true;
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
                    currentStandings = r;
                    this.downloadingStandings = false;
        }).whenComplete((msg, ex) -> {
            standingsLoadException = ex.getCause();
            downloadingStandings = false;
        });
    }

    private void downloadGames(){
        this.downloadingGames = true;
        CompletableFuture scheduledGames = downloadGamesFromLink(loginToken.getLinks().get(UserLoginToken.LINK_SCHEDULED_GAMES));

        CompletableFuture gameWithResult = downloadGamesFromLink(loginToken.getLinks().get(UserLoginToken.LINK_GAMES_WITH_RESULTS));

        CompletableFuture gamesWithoutResult = downloadGamesFromLink(loginToken.getLinks().get(UserLoginToken.LINK_GAMES_MISSING_RESULTS));

        CompletableFuture.allOf(gamesWithoutResult, gameWithResult, scheduledGames)
                .whenComplete((result, ex) -> this.downloadingGames = false);
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
                if (games.stream().noneMatch(g -> checkEventIsCached(g))) { //check we have relevant event data
                    downloadEvents();
                }
                gameSet.addAll(games);
            } catch (ParseException e){ //pass exception on to whenComplete function
                throw new CompletionException(e);
            }
        }).whenComplete( //catches any exceptions that occur/were passed on
                (msg, ex) -> gameLoadException = ex.getCause()
        );
        return future;
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

    private void loadEvents(){
        File file = new File(context.getFilesDir(), "events.txt");
        if(file.exists()) {
            try { //try read events file
                readEvents();
            } catch (Exception e) { //if reading fails, just download it
                downloadEvents();
            }
        } else {
            downloadEvents();
        }
    }

    private void downloadEvents() {
        this.downloadingEvents = true;
            CompletableFuture.supplyAsync(() -> {
                        try {
                            return downloadWebPage(HOME_URL);
                        } catch (Exception | InvalidLinkException e) {
                            throw new CompletionException(e);
                        }
                    }
            ).thenAcceptAsync(r -> {
                try {
                    Set<Event> events = WDSParser.parseEvents(r);
                    events.parallelStream().forEach(event -> event.setTeams(downloadEventTeams(event.getEventLink())));
                    this.eventSet.addAll(events);
                    this.downloadingEvents = false;
                    if (config.getCacheEvents()) {
                        writeEvents(events);
                    }
                } catch(ParseException e){
                    throw new CompletionException(e);
                }
            }).whenComplete( //catches any exceptions that occur/were passed on
                    (msg, ex) -> { eventLoadException = ex.getCause(); this.downloadingEvents = false; }
            );
    }

    private void writeEvents(Set<Event> events){
        //Delete old events in preparation for new events to be saved
        File eventsFile = new File(context.getFilesDir(), "events.txt");
        if(eventsFile.exists()){
            eventsFile.delete();
        }

        try (FileOutputStream fout = context.openFileOutput("events.txt", Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fout)
        ){
            oos.writeObject(events);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readEvents() throws IOException, ClassNotFoundException {
        try (FileInputStream fin = context.openFileInput("events.txt"); ObjectInputStream oin = new ObjectInputStream(fin)) {
                eventSet = (Set<Event>) oin.readObject();
        }
    }

    private Set<Team> downloadEventTeams(String eventLink) {

        CompletableFuture<Set<Team>> teamsPage1 = downloadEventTeamsPage(eventLink, 1);
        CompletableFuture<Set<Team>> teamsPage2 = downloadEventTeamsPage(eventLink, 2);
        CompletableFuture<Set<Team>> teamsPage3 = downloadEventTeamsPage(eventLink, 3);

        CompletableFuture combinedFutures = CompletableFuture.allOf(teamsPage1, teamsPage2, teamsPage3)
                .thenApply(r -> Stream.of(teamsPage1, teamsPage2, teamsPage3).flatMap(f -> f.join().stream()).collect(Collectors.toSet()))
                .whenComplete(
                        (msg, ex) -> eventLoadException = ex
                );

        return (Set<Team>) combinedFutures.join();
    }

    private CompletableFuture downloadEventTeamsPage(String eventLink, int pageNum){
        return CompletableFuture.supplyAsync(() ->
                {
                    try {
                        return downloadWebPage(eventLink+"/teams?page="+pageNum);
                    } catch (Exception | InvalidLinkException e) {
                        throw new CompletionException(e);
                    }
                }
        ).thenApply(r -> {
            try {
                return WDSParser.parseTeams(r);
            } catch (ParseException e) {
                throw new CompletionException(e);
            }
        });
    }

    private void downloadOAuthKey() {

        final String WEB_URL = "https://wds.usetopscore.com/u/oauth-key";

        CompletableFuture.supplyAsync(() -> {
            try {
                return downloadWebPage(WEB_URL);
            } catch (InvalidLinkException | IOException e) {
                throw new CompletionException(e);
            }
        }).thenApply(doc -> {
            //grab client_id and client_secret from web page
            Element table = doc.getElementsByClass("table no-border").first();
            List<String> oAuthInfo = new ArrayList<>();

            for (Element row : table.getElementsByTag("tr")) { //find id and secret in table
                oAuthInfo.add(row.getElementsByTag("td").first().text());
            }

            oAuthInfo.remove(2); //last row is a useless string
            return oAuthInfo;

        }).thenApply(oAuthInfo -> {
            try {
                Document response = Jsoup.connect("https://wds.usetopscore.com/api/oauth/server")
                        .userAgent(USER_AGENT)
                        .data("grant_type", "client_credentials")
                        .data("client_id", oAuthInfo.get(0))
                        .data("client_secret", oAuthInfo.get(1))
                        .ignoreContentType(true)
                        .post();
                //Take oauth token from JSON response
                JSONObject result = new JSONObject(response.body().text());

                return result.get("access_token");
            } catch (JSONException | IOException e) {
                throw new CompletionException(e);
            }
        }).thenAccept(r -> OAuthToken = (String) r)
        .whenComplete((msg, ex) -> { //catch & handle any errors that occurred
            //Todo: manage OAUTHKey errors
        });
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

    @Override
    public String toString() {
        return "DataManager{" +
                "gameSet=" + gameSet +
                ", requestQueue=" + requestQueue +
                '}';
    }
}