package com.macneet.wuapp.model;

import android.os.Bundle;
import android.os.Parcel;

import java.io.Serializable;
import java.util.HashMap;

/**
 * The UserLoginToken class is used to store the cookies and web page links generated when the user
 * logs in.
 */
public class UserLoginToken implements Serializable {

    private static final long serialVersionUID = 42L;

    public static final String LINK_USER = "linkuser";
    public static final String LINK_SCHEDULED_GAMES = "linkscheduledgames";
    public static final String LINK_GAMES_WITH_RESULTS = "linkgameswithresults";
    public static final String LINK_GAMES_MISSING_RESULTS = "linkgameswithoutresults";


    public void setCookies(HashMap<String, String> cookies) {
        this.cookies = cookies;
    }

    public void setLinks(HashMap<String, String> links) {
        this.links = links;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    private HashMap<String ,String> cookies;
    private HashMap<String ,String> links;
    private String name;
    private String profileImage;
    private String clientSecret;
    private String clientID;
    private String personID;

    public UserLoginToken(HashMap<String ,String> cookies, HashMap<String ,String> links, String name, String profileImage, String clientSecret, String clientID, String personID){
        this.cookies = cookies;
        this.links = links;
        this.name = name;
        this.profileImage = profileImage;
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.personID = personID;
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public HashMap<String, String> getCookies() {
        return cookies;
    }

    public HashMap<String, String> getLinks() {
        return links;
    }

    public String getName() {
        return name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getPersonID() { return personID; }
}
