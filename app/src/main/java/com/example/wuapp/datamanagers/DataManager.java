package com.example.wuapp.datamanagers;

import android.os.Handler;

import com.example.wuapp.exceptions.InvalidLinkException;
import com.example.wuapp.model.UserLoginToken;

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
        System.out.println(link);
        if(link == null) { throw new InvalidLinkException(); }
        return Jsoup.connect(link)
                .method(Connection.Method.GET)
                .userAgent(USER_AGENT)
                .cookies(loginToken.getCookies())
                .execute()
                .parse();
    }

    public boolean isDownloading(){ return downloading; }

    public UserLoginToken getLoginToken(){ return this.loginToken; }

    protected void submitResponse(DataReceiver.Response response){
        int delay = 100; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                request.callback.receiveResponse(response);
            }
        }, delay);
    }
}
