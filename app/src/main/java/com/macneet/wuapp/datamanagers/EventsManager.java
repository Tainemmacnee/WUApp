package com.macneet.wuapp.datamanagers;

import android.content.Context;

import com.macneet.wuapp.parsers.WDSParser;
import com.macneet.wuapp.exceptions.InvalidLinkException;
import com.macneet.wuapp.model.Event;
import com.macneet.wuapp.model.Team;
import com.macneet.wuapp.model.UserLoginToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventsManager extends DataManager {

    private static EventsManager eventsManager;
    public static final String REQUEST_EVENTS = "request_events";

    private Context context;
    private Set<Event> events = new HashSet<>();

    private EventsManager(UserLoginToken loginToken, Context context){
        this.loginToken = loginToken;
        this.context = context;
        loadEvents();
    }

    public static void initialise(UserLoginToken loginToken, Context context) {
        if(eventsManager == null) {
            eventsManager = new EventsManager(loginToken, context);
        }
    }

    public static EventsManager getInstance(){
        return eventsManager;
    }

    @Override
    public void requestData(DataReceiver.Request request) {
        this.request = request;
        if(!downloading) { prepareResponse(); }
    }

    @Override
    public void reload() {
        File file = new File(context.getFilesDir(), "events.txt");
        if(file.exists()) {
            file.delete();
        }
        events = new HashSet<>();
        exception = null;
        loadEvents();
    }

    private void prepareResponse(){
        if(request == null) { return; }
        submitResponse(new DataReceiver.Response<>(new ArrayList<>(events), exception, request));
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
        this.downloading = true;
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
                this.events.addAll(events);
                this.downloading = false;
                prepareResponse();
                if (ConfigManager.getInstance().getCacheEvents()) {
                    writeEvents(events);
                }
            } catch(ParseException e){
                throw new CompletionException(e);
            }
        }).whenComplete( //catches any exceptions that occur/were passed on
                (msg, ex) -> { exception = ex.getCause(); this.downloading = false; prepareResponse(); }
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
            events = (Set<Event>) oin.readObject();
        }
    }

    private Set<Team> downloadEventTeams(String eventLink) {

        CompletableFuture<Set<Team>> teamsPage1 = downloadEventTeamsPage(eventLink, 1);
        CompletableFuture<Set<Team>> teamsPage2 = downloadEventTeamsPage(eventLink, 2);
        CompletableFuture<Set<Team>> teamsPage3 = downloadEventTeamsPage(eventLink, 3);

        CompletableFuture combinedFutures = CompletableFuture.allOf(teamsPage1, teamsPage2, teamsPage3)
                .thenApply(r -> Stream.of(teamsPage1, teamsPage2, teamsPage3).flatMap(f -> f.join().stream()).collect(Collectors.toSet()))
                .whenComplete(
                        (msg, ex) -> exception = ex
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
}
