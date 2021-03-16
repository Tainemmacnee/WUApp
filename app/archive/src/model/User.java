package com.example.wuapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
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
    public static final String GAMESLINK = "gamesLink";

    private Map<String, String> profileInfo = new HashMap<String, String>();
    private String name, profileImgUrl, aboutText;
    private HashMap<String, String> links;
    private HashMap<String, String> cookies;

    private Future<List<Game>> futureGames;
    private Future<List<Event>> futureEvents;

    public Map<String, String> getProfileInfo() { return profileInfo; }

    public String getAboutText() { return aboutText; }

    public String getProfileImgUrl() { return this.profileImgUrl; }

    public HashMap<String, String> getCookies() { return this.cookies; }

    public String getName() { return this.name; }

    public HashMap<String, String> getLinks() { return this.links; }

    public boolean gamesDone(){ return futureGames.isDone(); }

    public boolean eventsDone(){ return futureEvents.isDone(); }

    public User(UserLoginToken loginToken){
        this.cookies = loginToken.getCookies();
        this.links = loginToken.getLinks();
        this.name = loginToken.getName();
        this.profileImgUrl = loginToken.getProfileImage();
    }

    /**
     * This function is used to start the concurrent loading of data that may be used later. This
     * function may also be called to restart/reload this data
     */
    public void loadData(){
        futureEvents = WebLoader.LoadEvents(cookies);
        futureGames = WebLoader.LoadGames(cookies, links.get(GAMESLINK));
    }

    public void setData(Future<List<Event>> futureEvents, Future<List<Game>> futureGames){
        this.futureEvents = futureEvents;
        this.futureGames = futureGames;
    }

    /**
     * This function retrieves the Game objects from the future and sorts them by date
     * @return An List of the users Games or an empty list if it fails to get from the future
     */
    public List<Game> getGames(){
        ArrayList<Game> gameAsList = new ArrayList<>();
        try {
            gameAsList = (ArrayList<Game>) futureGames.get();
            Collections.sort(gameAsList, new Game.SortByMostRecentDate());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return gameAsList;
    }

    /**
     * This function retrieves the Event objects from the future
     * @return An Array of the users Events or an empty list if it fails to get from the future
     */
    public List<Event> getEvents(){
        ArrayList<Event> eventsAsList = new ArrayList<>();
        try {
            eventsAsList = (ArrayList<Event>) futureEvents.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return eventsAsList;
    }

    /**
     * This function returns the users games filtered to only be the games that are upcoming
     * @return A list of Games that have not yet happened
     */
    public List<Game> getUpcomingGames(){
        ArrayList<Game> gameAsList = new ArrayList<>();
        for(Game g : getGames()){
            if(g.isUpcoming()){ gameAsList.add(g); }
        }
        return gameAsList;
    }

    /**
     * This function returns a list of the users games filtered to only contain games that can have
     * their scored changed.
     * @return A list of games that can have results reported.
     */
    public List<Game> getMissingResultGames() {
        ArrayList<Game> gameAsList = new ArrayList<>();
        for(Game g : getGames()){
            if(g.isReportable()){ gameAsList.add(g); }
        }
        return gameAsList;
    }

    /**
     * This function finds the event object matching the given name assuming the user is apart of
     * said event.
     * @param eventName The name of the event to find
     * @return This function returns a specific event matching the given name or null if no event is
     *          found
     */
    public Event getEvent(String eventName){
        for(Event e : getEvents()){
            if(e.getName().equals(eventName)){
                return e;
            }
        }
        return null;
    }

    /**
     * This function finds if there is a nickname in the users name
     * @return The users nickname if one exists, the users name otherwise
     */
    public String getNickName() {
        if(this.name.contains("\"")){
            String nickname = this.name.split(" ")[1];
            return nickname.substring(1, nickname.length()-1); //clip quotation marks
        }
        return this.name;
    }

}
