package com.macneet.wuapp.datamanagers;

import com.macneet.wuapp.model.UserLoginToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class OAuthManager extends DataManager {

    private static OAuthManager oAuthManager;
    public final String OAUTH_URL = "https://wds.usetopscore.com/u/oauth-key";
    public static final String REQUEST_OAUTH_TOKEN = "requestoauthtoken";
    private String oAuthToken;

    private OAuthManager(UserLoginToken loginToken){
        this.loginToken = loginToken;
    }

    public static void initialise(UserLoginToken loginToken){
        if(oAuthManager == null) {
            oAuthManager = new OAuthManager(loginToken);
        }
    }

    public static OAuthManager getInstance() { return oAuthManager; }

    @Override
    public void requestData(DataReceiver.Request request) {
        this.request = request;
        downloadOAuthToken(loginToken.getClientID(), loginToken.getClientSecret());
    }

    @Override
    public void reload() {
        exception = null;
    }

    private void prepareResponse(){
        if(request == null) { return; }
        ArrayList<String> data = new ArrayList<>();
        data.add(oAuthToken);
        submitResponse(new DataReceiver.Response(data, exception, request));
    }

    private void downloadOAuthToken(String clientID, String clientSecret){
        downloading = true;
        CompletableFuture.supplyAsync(() -> {
            try {
                Document response = Jsoup.connect("https://wds.usetopscore.com/api/oauth/server")
                        .userAgent(USER_AGENT)
                        .data("grant_type", "client_credentials")
                        .data("client_id", clientID)
                        .data("client_secret", clientSecret)
                        .ignoreContentType(true)
                        .post();
                //Take oauth token from JSON response
                JSONObject result = new JSONObject(response.body().text());
                return result.get("access_token");
            } catch (JSONException | IOException e) {
                throw new CompletionException(e);
            }
        }).thenAccept(r -> {
            oAuthToken = (String) r;
        }
        ).whenComplete((msg, ex) -> { //catch & handle any errors that occurred
            exception = ex;
            downloading = false;
            prepareResponse();
        });
    }
}
