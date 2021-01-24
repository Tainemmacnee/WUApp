package com.example.wuapp.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * The UserLoginToken class is used to store the cookies and web page links generated when the user
 * logs in.
 */
public class UserLoginToken implements Serializable {

    public static final String LINK_USER = "linkuser";
    public static final String LINK_SCHEDULED_GAMES = "linkscheduledgames";
    public static final String LINK_GAMES_WITH_RESULTS = "linkgameswithresults";
    public static final String LINK_GAMES_MISSING_RESULTS = "linkgameswithoutresults";


    private HashMap<String ,String> cookies;
    private HashMap<String ,String> links;
    private String name;
    private String profileImage;

    public UserLoginToken(HashMap<String ,String> cookies, HashMap<String ,String> links, String name, String profileImage){
        this.cookies = cookies;
        this.links = links;
        this.name = name;
        this.profileImage = profileImage;
    }

    public HashMap<String, String> getCookies() {
        return cookies;
    }

    public HashMap<String, String> getLinks() {
        return links;
    }

    public void setCookies(HashMap<String, String> cookies) {
        this.cookies = cookies;
    }

    public void setLinks(HashMap<String, String> links) {
        this.links = links;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
