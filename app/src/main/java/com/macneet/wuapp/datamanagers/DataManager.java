package com.macneet.wuapp.datamanagers;

import android.os.Handler;

import com.macneet.wuapp.exceptions.InvalidLinkException;
import com.macneet.wuapp.model.UserLoginToken;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public abstract class DataManager {

    protected static Handler handler = new Handler();

    protected UserLoginToken loginToken;
    protected DataReceiver.Request request;
    protected boolean downloading = false;
    protected Throwable exception;

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
    public static final String HOME_URL = "https://wds.usetopscore.com";

    public abstract void requestData(DataReceiver.Request request);

    public abstract void reload();

    protected Document downloadWebPage(String link) throws IOException, InvalidLinkException {
        if(link == null) { throw new InvalidLinkException(); }
        return Jsoup.connect(link)
                .method(Connection.Method.GET)
                .userAgent(USER_AGENT)
                .cookies(loginToken.getCookies())
                .execute()
                .parse();
    }

    public UserLoginToken getLoginToken(){ return this.loginToken; }

    /**
     * This function is used to send the response to the Data Receiver. The response is delayed to avoid
     * sending the response too early.
     * @param response The response to send
     */
    protected void submitResponse(DataReceiver.Response response){
        int delay = 100; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                request.callback.receiveResponse(response);
            }
        }, delay);
    }
}
