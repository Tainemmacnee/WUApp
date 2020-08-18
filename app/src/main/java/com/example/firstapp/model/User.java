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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class User {

        public static final String UPCOMINGGAMESLINK = "upcomingGamesLink";
        public static final String USERPAGELINK = "userPageLink";

        private String name, profileImgUrl, gId, age, dHand, aboutText;
        public Future<List<Event>> futureEvents;
        private Future<List<Game>> futureUpcomingGames;
        private HashMap<String, String> links;
        private HashMap<String, String> cookies;

    public User(HashMap<String, String> cookies, HashMap<String, String> links){
        //Load user data from wds.usetopscore.com with cookies

        this.links = links;
        this.cookies = cookies;

        loadExtras();
        try {
            loadUser().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void loadExtras(){
        System.out.println("BIG L: "+links);
        futureEvents = Event.LoadEvents(cookies);
        futureUpcomingGames = Game.LoadUpcomingGames(cookies, links.get(User.UPCOMINGGAMESLINK));
    }

    public Event[] getEvents(){
        ArrayList<Event> eventsAsList = null;
        try {
            eventsAsList = (ArrayList<Event>) futureEvents.get();
            System.out.println("EVENTS LOADED");

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return eventsAsList.toArray(new Event[eventsAsList.size()]);
    }

    public Game[] getUpcomingGames(){
        ArrayList<Game> gameAsList = null;
        try {
            gameAsList = (ArrayList<Game>) futureUpcomingGames.get();
            System.out.println("UPCOMING GAMES LOADED");
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
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return new UserLoginToken(cookies, links);
        });
    }


    private Future<?> loadUser() {
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        final String WEB_URL = "https://wds.usetopscore.com";
        ExecutorService executor = Executors.newCachedThreadPool();

        return executor.submit(() -> {
            String NEW_URL = WEB_URL + links.get(User.USERPAGELINK);
            Connection.Response loadPageResponse = null;
            try {
                loadPageResponse = Jsoup.connect(NEW_URL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .cookies(cookies)
                        .execute();


            Document doc = loadPageResponse.parse();

            //Collect username from page
            Element profileNameDiv = doc.getElementsByClass("profile-name").first();
            Element profileName = profileNameDiv.child(0);
            name = profileName.text().trim();

            //collect profile info
            Element profileInfoElem = doc.getElementsByClass("profile-info").first().child(0);
            gId = profileInfoElem.child(1).text();
            age = profileInfoElem.child(3).text();
            dHand = profileInfoElem.child(5).text();

            //Collect user image url from page
            Element profilePicDiv = doc.getElementsByClass("profile-image").first();
            Element profilePic = profilePicDiv.child(0);
            profileImgUrl = profilePic.attr("src");

            Element profileAboutDiv = doc.getElementsByClass("profile-about").first();
            Element profileAboutText = profileAboutDiv.getElementsByClass("rich-text").first();
            aboutText = profileAboutText.text();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        });
    }

    public String getName() {
        return this.name;
    }

    public String getAge() {
        return age;
    }

    public String getdHand() {
        return dHand;
    }

    public String getgId() {
        return gId;
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
