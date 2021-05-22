package com.macneet.wuapp.activities.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.macneet.wuapp.R;
import com.macneet.wuapp.datamanagers.ConfigManager;
import com.macneet.wuapp.datamanagers.DataManager;
import com.macneet.wuapp.databinding.ActivityLoginBinding;
import com.macneet.wuapp.exceptions.InvalidLinkException;
import com.macneet.wuapp.model.UserLoginToken;
import com.macneet.wuapp.activities.main.MainActivity;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    public final String LOGIN_URL = "https://wds.usetopscore.com";
    public final String OAUTH_URL = "https://wds.usetopscore.com/u/oauth-key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        binding.button.setOnClickListener(this::attemptLogin);

        setContentView(view);
    }

    public void onStart() {
        super.onStart();
        //check config to see if we should use saved login
        ConfigManager.initialise(this);
        ConfigManager config = ConfigManager.getInstance();
        if(config.getCacheLogin()) {

            //load login token
            UserLoginToken loginToken = null;
            try (FileInputStream fin = getApplicationContext().openFileInput("login.txt"); ObjectInputStream oin = new ObjectInputStream(fin)) {
                loginToken = (UserLoginToken) oin.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (loginToken != null) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(getString(R.string.MESSAGE_LOGINTOKEN), loginToken);
                startActivity(intent);
            }
        }
    }

    private Document loadLoginPage(){
        Document result = null;
        try {
            Connection.Response loadPageResponse = Jsoup.connect(LOGIN_URL)
                    .method(Connection.Method.GET)
                    .userAgent(DataManager.USER_AGENT)
                    .execute();
            result = loadPageResponse.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private UserLoginToken attemptLogin(Document loginPage, String username, String password){

        //Set username
        Element emailField = loginPage.getElementById("signin_email");
        emailField.val(username);

        //Set password
        Element passwordField = loginPage.getElementById("signin_password");
        passwordField.val(password);

        //find login form
        FormElement loginForm = (FormElement) loginPage.getElementsByClass("form-vertical signin exists spacer1").first();

        //submit login form
        Connection.Response loginActionResponse;
        try {
            loginActionResponse = loginForm.submit()
                    .userAgent(DataManager.USER_AGENT)
                    .execute();

            loginPage = loginActionResponse.parse();
        } catch (IOException e) {
            //submission failed/login failed
            return null;
        }

        //Collect username
        Element userButton = loginPage.getElementsByClass("global-toolbar-user-btn ").first();
        String name = userButton.child(1).text();
        //Collect profile image
        String profileImage = userButton.child(0).attr("src").replace("30", "200");
        //Collect login cookies
        HashMap<String, String> cookies = (HashMap<String, String>) loginActionResponse.cookies();
        //collect links needed to download more data
        HashMap<String, String> links = new HashMap<>();
        links.put(UserLoginToken.LINK_USER, LOGIN_URL+userButton.attr("href"));
        links.put(UserLoginToken.LINK_SCHEDULED_GAMES, LOGIN_URL+userButton.attr("href") + "/schedule");
        links.put(UserLoginToken.LINK_GAMES_WITH_RESULTS, LOGIN_URL+userButton.attr("href") + "/schedule/game_type/with_result");
        links.put(UserLoginToken.LINK_GAMES_MISSING_RESULTS, LOGIN_URL+userButton.attr("href") + "/schedule/game_type/missing_result");

        List<String> oAuthDetails = generateOAuthDetails(cookies);
        String personID = generatePersonID(cookies);
        if(oAuthDetails == null || personID == null) { return null; }

        return new UserLoginToken(cookies, links, name, profileImage, oAuthDetails.get(1), oAuthDetails.get(0), personID);
    }

    private String generatePersonID(HashMap<String, String> cookies){
        try { //download web page with oAuth Credentials
            Connection.Response response = Jsoup.connect("https://wds.usetopscore.com/api/me")
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .userAgent(DataManager.USER_AGENT)
                    .cookies(cookies)
                    .execute();

            JSONObject result = (JSONObject) new JSONObject(response.body()).getJSONArray("result").get(0);
            return ""+ result.get("person_id");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<String> generateOAuthDetails(HashMap<String, String> cookies){
        Document oAuthPage;
        try { //download web page with oAuth Credentials
            Connection.Response loadPageResponse = Jsoup.connect(OAUTH_URL)
                    .method(Connection.Method.GET)
                    .userAgent(DataManager.USER_AGENT)
                    .cookies(cookies)
                    .execute();
            oAuthPage = loadPageResponse.parse();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //grab client_id and client_secret from web page
        Element table = oAuthPage.getElementsByClass("table no-border").first();
        List<String> oAuthDetails = new ArrayList<>();

        for (Element row : table.getElementsByTag("tr")) { //find id and secret in table
            oAuthDetails.add(row.getElementsByTag("td").first().text());
        }
        return oAuthDetails;
    }

    public void attemptLogin(View view){
        String username = binding.editText.getText().toString();
        String password = binding.editText2.getText().toString();

        ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "",
                "Logging in. Please wait...", true);

        CompletableFuture.supplyAsync(this::loadLoginPage)
                .thenApply(r -> attemptLogin(r, username, password))
                .thenAccept(r -> {
                    dialog.dismiss();
                    if(r != null) {
                        saveLoginToken(r);

                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra(getString(R.string.MESSAGE_LOGINTOKEN), r);
                        startActivity(intent);
                    } else {
                        Snackbar.make(findViewById(R.id.login_layout), "Login Failed", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveLoginToken(UserLoginToken token) {
        //delete login if one is already saved
        File login = new File(getApplicationContext().getFilesDir(), "login.txt");
        if (login.exists()) {
            login.delete();
        }
        //save login to file
        try (FileOutputStream fout = getApplicationContext().openFileOutput("login.txt", Context.MODE_PRIVATE); ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(token);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}