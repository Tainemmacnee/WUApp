package com.example.Wuapp.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * The UserLoginToken class is used to store the cookies and web page links generated when the user
 * logs in.
 */
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
