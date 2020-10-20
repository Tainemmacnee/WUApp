package com.example.Wuapp.model;

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
    public Future<List<Event>> futureEvents;
    private HashMap<String, String> links;
    private HashMap<String, String> cookies;
    private Future<List<Game>> futureGames;

    public Map<String, String> getProfileInfo() { return profileInfo; }

    public String getAboutText() { return aboutText; }

    public String getProfileImgUrl() { return this.profileImgUrl; }

    public HashMap<String, String> getCookies() { return this.cookies; }

    public String getName() { return this.name; }

    public HashMap<String, String> getLinks() { return this.links; }

    public boolean gamesDone(){ return futureGames.isDone(); }

    public boolean eventsDone(){ return futureEvents.isDone(); }

    public User(UserLoginToken loginToken, String name, String profileImgUrl, String aboutText, Map<String, String> profileInfo){
        this.cookies = loginToken.getCookies();
        this.links = loginToken.getLinks();
        this.name = name;
        this.profileImgUrl = profileImgUrl;
        this.aboutText = aboutText;
        this.profileInfo = profileInfo;

        //loadData();
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
     * This function retrieves the Event objects from the future object and converts them from a list
     * to an array for use in adapters.
     * @return An Array of the users Events
     */
    public Event[] getEventsAsArray(){
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
        Event[] events = getEventsAsArray();
        for(Event e : events){
            if(e.getName().equals(eventName)){
                return e;
            }
        }
        return null;
    }

    /**
     *  This function retrieves the Game objects from the future, then sorts and converts them from
     *  a list to an array for use in adapters.
     * @return
     */
    public Game[] getGamesAsArray(){
        ArrayList<Game> gameAsList = new ArrayList<>();
        try {
            gameAsList = (ArrayList<Game>) futureGames.get();
            Collections.sort(gameAsList, new Game.SortByDate());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return gameAsList.toArray(new Game[gameAsList.size()]);
    }

    /**
     * This function is the same as getGamesAsArray but filters for upcoming games only.
     * @return An array of the users upcoming games
     */
    public Game[] getUpcomingGamesAsArray(){
        ArrayList<Game> gameAsList = new ArrayList<>();
        try {
            for(Game g : futureGames.get()){
                if(g.isUpcoming()){ gameAsList.add(g); }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return gameAsList.toArray(new Game[gameAsList.size()]);
    }

    /**
     * This function is the same as getGamesAsArray but filters for missing result games only.
     * @return An array of the users games with missing results
     */
    public Game[] getMissingResultGamesAsArray() {
        ArrayList<Game> gameAsList = new ArrayList<>();
        try {
            for(Game g : futureGames.get()){
                if(g.isReportable()){ gameAsList.add(g); }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return gameAsList.toArray(new Game[gameAsList.size()]);
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
