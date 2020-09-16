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

    public void loadExtras(){
        futureEvents = WebLoader.LoadEvents(cookies);
        futureUpcomingGames = WebLoader.LoadUpcomingGames(cookies, links.get(User.UPCOMINGGAMESLINK));
        futureMissingResultGames = WebLoader.LoadMissingResultsGames(cookies, links.get(User.MISSINGRESULTSLINK));
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

    public Event getEvent(String eventName){
        Event[] events = getEvents();
        for(Event e : events){
            if(e.getName().equals(eventName)){
                return e;
            }
        }
        return null;
    }

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

    public static Future<UserLoginToken> loginUser(String username, String password) {
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        final String WEB_URL = "https://wds.usetopscore.com";
        ExecutorService executor = Executors.newCachedThreadPool();

        return executor.submit(() -> {
            HashMap<String, String> cookies = null;
            HashMap<String, String> links = new HashMap<>();
            String name = null;
            String profileImgUrl = null;
            String gId, age, dHand;

            try {
                Connection.Response loginFormResponse = Jsoup.connect(WEB_URL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .execute();

                //log user in
                Element loginForm = loginFormResponse.parse()
                        .getElementsByClass("form-vertical signin exists spacer1").first();

                Element emailField = loginForm.getElementsByClass("span3 full initial-focus span3 mailcheck").first();
                emailField.val(username);

                Element passwordField = loginForm.getElementById("signin_password");
                passwordField.val(password);

                FormElement form = (FormElement) loginForm;
                Connection.Response loginActionResponse = form.submit()
                        .cookies(loginFormResponse.cookies())
                        .userAgent(USER_AGENT)
                        .execute();

                cookies = (HashMap) loginActionResponse.cookies();

                Document doc = loginActionResponse.parse();
                Element userpageLink = doc.getElementsByClass("global-toolbar-user-btn ").first();

                //Collect link to upcoming schedule for later web scraping
                links.put(User.USERPAGELINK, userpageLink.attr("href"));
                links.put(User.UPCOMINGGAMESLINK, userpageLink.attr("href") + "/schedule");
                links.put(User.MISSINGRESULTSLINK, userpageLink.attr("href") + "/schedule/game_type/missing_result");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return new UserLoginToken(cookies, links);
        });
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
