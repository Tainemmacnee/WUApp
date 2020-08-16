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

public class User {

        public static final String UPCOMINGGAMESLINK = "upcomingGamesLink";

        private String name, profileImgUrl;
        public Future<List<Event>> futureEvents;
        private Future<List<UpcomingGame>> futureUpcomingGames;
        private HashMap<String, String> links;
        private HashMap<String, String> cookies;

    public User(HashMap<String, String> cookies, String name, String profileImgUrl, HashMap<String, String> links){
        //Load user data from wds.usetopscore.com with cookies

        this.name = name;
        this.profileImgUrl = profileImgUrl;
        this.links = links;
        this.cookies = cookies;
    }

    public void loadExtras(){
        System.out.println("BIG L: "+links);
        futureEvents = Event.LoadEvents(cookies);
        futureUpcomingGames = UpcomingGame.LoadUpcomingGames(cookies, links.get(User.UPCOMINGGAMESLINK));

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

    public static Future<User> loadUser(String username, String password) {
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        final String WEB_URL = "https://wds.usetopscore.com";
        ExecutorService executor = Executors.newCachedThreadPool();

        return executor.submit(() -> {
            HashMap<String, String> cookies = null;
            HashMap<String, String> links = new HashMap<>();
            String name = null;
            String profileImgUrl = null;


            try {
                Connection.Response loginFormResponse = Jsoup.connect(WEB_URL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
//                        .cookies(input)
                        .execute();

                //log user in
                Element loginForm = loginFormResponse.parse()
                        .getElementsByClass("form-vertical signin exists spacer1").first();

                Element emailField = loginForm.getElementsByClass("span3 full initial-focus span3 mailcheck").first();
                emailField.val(username);

                Element passwordField = loginForm.getElementById("signin_password");
                passwordField.val(password);

                FormElement form = (FormElement)loginForm;
                Connection.Response loginActionResponse = form.submit()
                        .cookies(loginFormResponse.cookies())
                        .userAgent(USER_AGENT)
                        .execute();

                cookies = (HashMap)loginActionResponse.cookies();

                Document doc = loginActionResponse.parse();
                Element userpageLink = doc.getElementsByClass("global-toolbar-user-btn ").first();

                //Collect link to upcoming schedule for later web scraping
                links.put(User.UPCOMINGGAMESLINK, userpageLink.attr("href")+"/schedule");

                String NEW_URL = WEB_URL + userpageLink.attr("href");
                Connection.Response loadPageResponse = Jsoup.connect(NEW_URL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .cookies(cookies)
                        .execute();

                doc = loadPageResponse.parse();

                //Collect username from page
                Element profileNameDiv = doc.getElementsByClass("profile-name").first();
                Element profileName = profileNameDiv.child(0);
                name = profileName.text().trim();

                //Collect user image url from page
                Element profilePicDiv = profileNameDiv.nextElementSibling();
                Element profilePic = profilePicDiv.child(0);
                profileImgUrl =  profilePic.attr("src");
            } catch (IOException e) {
                return null;
            }
            return new User(cookies, name, profileImgUrl, links);
        });
    }



    public String getName() {
        return this.name;
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
