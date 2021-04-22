package com.macneet.wuapp.datamanagers;

import android.content.Context;

import com.macneet.wuapp.exceptions.InvalidLinkException;
import com.macneet.wuapp.model.UserLoginToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class OAuthManager extends DataManager {

    private static OAuthManager oAuthManager;
    public final String OAUTH_URL = "https://wds.usetopscore.com/u/oauth-key";
    public static final String REQUEST_OAUTH_TOKEN = "requestoauthtoken";
    private String oAuthToken;
    private Context context;

    private OAuthManager(UserLoginToken loginToken, Context context){
        this.loginToken = loginToken;
        this.context = context;
        oAuthToken = readToken(context);
        if(oAuthToken == null) { downloadOAuthKey(); } //if token still null, try download it
    }

    public static void initialise(UserLoginToken loginToken, Context context){
        if(oAuthManager == null) {
            oAuthManager = new OAuthManager(loginToken, context);
        }
    }

    public static OAuthManager getInstance() { return oAuthManager; }

    @Override
    public void requestData(DataReceiver.Request request) {
        this.request = request;
        if(!downloading) { prepareResponse(); }
    }

    @Override
    public void reload() {
        exception = null;
        if(oAuthToken != null) { return; }
        oAuthToken = readToken(context);
        if(oAuthToken == null) { downloadOAuthKey(); } //if token still null, try download it
    }

    private void prepareResponse(){
        if(request == null) { return; }
        ArrayList<String> data = new ArrayList<>();
        data.add(oAuthToken);
        submitResponse(new DataReceiver.Response(data, exception, request));
    }

    private static String readToken(Context context){
        String OAuthToken;
        try (FileInputStream fin = context.openFileInput("token.txt"); ObjectInputStream oin = new ObjectInputStream(fin)) {
            OAuthToken = (String) oin.readObject();
            return OAuthToken;
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
        }

        return null;
    }

    private void saveToken(Context context){
        File file = new File(context.getFilesDir(), "token.txt");
        if(file.exists()){
            file.delete();
        }

        try (FileOutputStream fout = context.openFileOutput("token.txt", Context.MODE_PRIVATE); ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(oAuthToken);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void downloadOAuthKey() {
        downloading = true;
        CompletableFuture.supplyAsync(() -> {
            try {
                return downloadWebPage(OAUTH_URL);
            } catch (InvalidLinkException | IOException e) {
                throw new CompletionException(e);
            }
        }).thenApply(doc -> {
            //grab client_id and client_secret from web page
            Element table = doc.getElementsByClass("table no-border").first();
            List<String> oAuthInfo = new ArrayList<>();

            for (Element row : table.getElementsByTag("tr")) { //find id and secret in table
                oAuthInfo.add(row.getElementsByTag("td").first().text());
            }

            oAuthInfo.remove(2); //last row is a useless string
            return oAuthInfo;

        }).thenApply(oAuthInfo -> {
            try {
                Document response = Jsoup.connect("https://wds.usetopscore.com/api/oauth/server")
                        .userAgent(USER_AGENT)
                        .data("grant_type", "client_credentials")
                        .data("client_id", oAuthInfo.get(0))
                        .data("client_secret", oAuthInfo.get(1))
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
            if(oAuthToken != null) { saveToken(context); } //save token if loaded
        }
        ).whenComplete((msg, ex) -> { //catch & handle any errors that occurred
            exception = ex;
            downloading = false;
            prepareResponse();
        });
    }
}
