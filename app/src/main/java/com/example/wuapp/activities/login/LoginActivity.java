package com.example.wuapp.activities.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wuapp.R;
import com.example.wuapp.datamanagers.ConfigManager;
import com.example.wuapp.datamanagers.DataManager;
import com.example.wuapp.databinding.ActivityLoginBinding;
import com.example.wuapp.model.UserLoginToken;
import com.example.wuapp.activities.main.MainActivity;
import com.google.android.material.snackbar.Snackbar;

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
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    public final String LOGIN_URL = "https://wds.usetopscore.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConfigManager.initialise(getApplicationContext());

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        binding.button.setOnClickListener(this::attemptLogin);

        setContentView(view);
    }

    public void onStart() {
        super.onStart();
        //check configManager to see if we should use saved login
        ConfigManager configManager = ConfigManager.getInstance();
        if(configManager.getCacheLogin()) {

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

        //Collect login data
        Element userButton = loginPage.getElementsByClass("global-toolbar-user-btn ").first();
        String name = userButton.child(1).text();

        String profileImage = userButton.child(0).attr("src").replace("30", "200");

        HashMap<String, String> cookies = (HashMap<String, String>) loginActionResponse.cookies();

        HashMap<String, String> links = new HashMap<>();
        links.put(UserLoginToken.LINK_USER, LOGIN_URL+userButton.attr("href"));
        links.put(UserLoginToken.LINK_SCHEDULED_GAMES, LOGIN_URL+userButton.attr("href") + "/schedule");
        links.put(UserLoginToken.LINK_GAMES_WITH_RESULTS, LOGIN_URL+userButton.attr("href") + "/schedule/game_type/with_result");
        links.put(UserLoginToken.LINK_GAMES_MISSING_RESULTS, LOGIN_URL+userButton.attr("href") + "/schedule/game_type/missing_result");

        return new UserLoginToken(cookies, links, name, profileImage);
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

    private void saveLoginToken(UserLoginToken token){
        if (!new File(getApplicationContext().getFilesDir(), "login.txt").exists()) {
            try (FileOutputStream fout = getApplicationContext().openFileOutput("login.txt", Context.MODE_PRIVATE); ObjectOutputStream oos = new ObjectOutputStream(fout)) {
                oos.writeObject(token);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}