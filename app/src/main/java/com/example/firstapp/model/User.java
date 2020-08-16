package com.example.firstapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class User {

        private String name, profileImgUrl;
        private Future<List<Event>> futureEvents;
        private Future<List<UpcomingGame>> futureUpcomingGames;
        private Map<String, String> links = new HashMap<>();
        private ExecutorService executor = Executors.newCachedThreadPool();

    public User(HashMap<String, String> cookies){
        //Load user data from wds.usetopscore.com with cookies
        try {
            futureEvents = Event.LoadEvents(cookies);
            Map<String, String> userdata = (Map<String, String>) loadUser(cookies).get();
            futureUpcomingGames = UpcomingGame.LoadUpcomingGames(cookies, userdata.get("upcomingGamesLink"));
            this.name = userdata.get("name");
            this.profileImgUrl = userdata.get("profileImgUrl");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Event[] getEvents(){
        ArrayList<Event> eventsAsList = null;
        try {
            eventsAsList = (ArrayList<Event>) futureEvents.get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return eventsAsList.toArray(new Event[eventsAsList.size()]);
    }

    public UpcomingGame[] getUpcomingGames(){
        ArrayList<UpcomingGame> UpcomingGameAsList = null;
        try {
            UpcomingGameAsList = (ArrayList<UpcomingGame>) futureUpcomingGames.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return UpcomingGameAsList.toArray(new UpcomingGame[UpcomingGameAsList.size()]);
    }

    public Future<?> loadUser(HashMap<String, String> input) {
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        final String WEB_URL = "https://wds.usetopscore.com";
        Map<String, String> output = new HashMap<>();

        return executor.submit(() -> {
            try {
                Connection.Response loadPageResponse = Jsoup.connect(WEB_URL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .cookies(input)
                        .execute();
                Document doc = loadPageResponse.parse();
                Element userpageLink = doc.getElementsByClass("global-toolbar-user-btn ").first();

                //Collect link to upcoming schedule for later web scraping
                output.put("upcomingGamesLink", userpageLink.attr("href")+"/schedule");

                String NEW_URL = WEB_URL + userpageLink.attr("href");
                loadPageResponse = Jsoup.connect(NEW_URL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .cookies(input)
                        .execute();
                doc = loadPageResponse.parse();

                //Collect username from page
                Element profileNameDiv = doc.getElementsByClass("profile-name").first();
                Element profileName = profileNameDiv.child(0);
                output.put("name", profileName.text().trim());

                //Collect user image url from page
                Element profilePicDiv = profileNameDiv.nextElementSibling();
                Element profilePic = profilePicDiv.child(0);
                output.put("profileImgUrl", profilePic.attr("src"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return output;
        });
    }



    public String getName() {
        return this.name;
    }

    public String getProfileImgUrl() {
        return this.profileImgUrl;
    }
}
