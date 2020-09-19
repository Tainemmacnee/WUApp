package com.example.firstapp.model;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The User class is used to store and manage all of the user related information that is gathered
 * from the website
 */
public class User{

        public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        public static final String UPCOMINGGAMESLINK = "upcomingGamesLink";
        public static final String MISSINGRESULTSLINK = "missingResultsLink";
        public static final String USERPAGELINK = "userPageLink";

        private Map<String, String> profileInfo = new HashMap<String, String>();;
        private String name, profileImgUrl, gId, age, dHand, aboutText;
        public Future<List<Event>> futureEvents;
        private Future<List<Game>> futureUpcomingGames;
        private Future<List<Game>> futureMissingResultGames;
        private HashMap<String, String> links;
        private HashMap<String, String> cookies;

    public User(UserLoginToken loginToken, String name, String profileImgUrl, String aboutText, Map<String, String> profileInfo){
        this.cookies = loginToken.getCookies();
        this.links = loginToken.getLinks();
        this.name = name;
        this.profileImgUrl = profileImgUrl;
        this.aboutText = aboutText;
        this.profileInfo = profileInfo;

        loadExtras();
    }

    /**
     * This function is used to start the concurrent loading of data that may be used later. This
     * function may also be called to restart/reload this data
     */
    public void loadExtras(){
        futureEvents = WebLoader.LoadEvents(cookies);
        futureUpcomingGames = WebLoader.LoadUpcomingGames(cookies, links.get(User.UPCOMINGGAMESLINK));
        futureMissingResultGames = WebLoader.LoadMissingResultsGames(cookies, links.get(User.MISSINGRESULTSLINK));
    }

    /**
     * This function retrieves the Event objects from the future object and converts them from a list
     * to an array for use in adapters.
     * @return An Array of the users Events
     */
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

    /**
     * This function finds the event object matching the given name assuming the user is apart of
     * said event.
     * @param eventName The name of the event to find
     * @return This function returns a specific event matching the given name or null if no event is
     *          found
     */
    public Event getEvent(String eventName){
        Event[] events = getEvents();
        for(Event e : events){
            if(e.getName().equals(eventName)){
                return e;
            }
        }
        return null;
    }

    /**
     * This function is the same as getEvents but for upcoming games.
     * @return An array of the users upcoming games
     */
    public Game[] getUpcomingGames(){
        ArrayList<Game> gameAsList = null;
        try {
            gameAsList = (ArrayList<Game>) futureUpcomingGames.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return gameAsList.toArray(new Game[gameAsList.size()]);
    }

    /**
     * This function is the same as getEvents but for games with missing results
     * @return An array of the users games with missing results
     */
    public Game[] getMissingResultGames() {
        ArrayList<Game> gameAsList = new ArrayList<>();
        try {
            gameAsList = (ArrayList<Game>) futureMissingResultGames.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return gameAsList.toArray(new Game[gameAsList.size()]);
    }

    public String getName() {
        return this.name;
    }

    public String getNickName() {
        if(this.name.contains("\"")){
            String nickname = this.name.split(" ")[1];
            return nickname.substring(1, nickname.length()-1); //clip quotation marks
        }
        return this.name;
    }

    public Map<String, String> getProfileInfo() {
        return profileInfo;
    }

    public String getAboutText() {
        return aboutText;
    }

    public String getProfileImgUrl() {
        return this.profileImgUrl;
    }

    public HashMap<String, String> getCookies() {
        return this.cookies;
    }

    public HashMap<String, String> getLinks() {
        return this.links;
    }

}
