package com.example.firstapp.model;

import java.io.Serializable;
import java.util.HashMap;

public class UserLoginToken implements Serializable {

    private HashMap<String ,String> cookies;
    private HashMap<String ,String> links;

    public UserLoginToken(HashMap<String ,String> cookies, HashMap<String ,String> links){
        this.cookies = cookies;
        this.links = links;
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
}
