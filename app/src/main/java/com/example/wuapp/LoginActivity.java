package com.example.wuapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.wuapp.data.DataManager;
import com.example.wuapp.databinding.FragmentDashboardBinding;
import com.example.wuapp.databinding.FragmentLoginBinding;
import com.example.wuapp.model.User;
import com.example.wuapp.model.UserLoginToken;
import com.example.wuapp.ui.login.LoginFragment;
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
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    FragmentLoginBinding binding;

    private String LOGIN_URL = "https://wds.usetopscore.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        binding.button.setOnClickListener(this::attemptLogin);

        setContentView(view);
    }

    public void onStart() {
        super.onStart();

        FileInputStream fin = null;
        ObjectInputStream oin = null;
        UserLoginToken loginToken = null;
        try {
            fin = getApplicationContext().openFileInput("login.txt");
            if(fin != null){
                oin = new ObjectInputStream(fin);
                loginToken = (UserLoginToken) oin.readObject();
            }
        }catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try { if (fin != null) fin.close(); } catch(IOException e) {}
            try { if (oin != null) oin.close(); } catch(IOException e) {}
        }

        if(loginToken != null) {
            Intent intent = new Intent(this, DisplayUserActivity.class);
            intent.putExtra(MainActivity.MESSAGE_LOGINTOKEN, loginToken);
            DataManager dm = new DataManager();
            intent.putExtra("test", dm);
            startActivity(intent);
        }
    }

    private Document loadLoginPage(){
        Document result = null;
        try {
            Connection.Response loadPageResponse = Jsoup.connect(LOGIN_URL)
                    .method(Connection.Method.GET)
                    .userAgent(User.USER_AGENT)
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
                    .userAgent(User.USER_AGENT)
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

        HashMap<String, String> cookies = (HashMap) loginActionResponse.cookies();

        HashMap<String, String> links = new HashMap<>();
        links.put(UserLoginToken.LINK_USER, userButton.attr("href"));
        links.put(UserLoginToken.LINK_SCHEDULED_GAMES, userButton.attr("href") + "/schedule");
        links.put(UserLoginToken.LINK_GAMES_WITH_RESULTS, userButton.attr("href") + "/schedule/game_type/played");
        links.put(UserLoginToken.LINK_GAMES_MISSING_RESULTS, userButton.attr("href") + "/schedule/game_type/missing_result");

        //TODO: remove legacy links
        links.put(User.USERPAGELINK, userButton.attr("href"));
        links.put(User.UPCOMINGGAMESLINK, userButton.attr("href") + "/schedule");
        links.put(User.MISSINGRESULTSLINK, userButton.attr("href") + "/schedule/game_type/missing_result");
        links.put(User.GAMESLINK, userButton.attr("href") + "/schedule/event_id/active_events_only/game_type/all");

        return new UserLoginToken(cookies, links, name, profileImage);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void attemptLogin(View view){
        String username = binding.editText.getText().toString();
        String password = binding.editText2.getText().toString();

        ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "",
                "Logging in. Please wait...", true);

        CompletableFuture.supplyAsync(() -> loadLoginPage()
        ).thenApply(r -> attemptLogin(r, username, password)
        ).thenAccept(r -> {
            dialog.dismiss();

            if(r != null) {
                saveLoginToken(r);

                Intent intent = new Intent(this, DisplayUserActivity.class);
                intent.putExtra(MainActivity.MESSAGE_LOGINTOKEN, r);
                DataManager dm = new DataManager();
                intent.putExtra("test", dm);
                startActivity(intent);
            } else {
                Snackbar.make(findViewById(R.id.login_layout), "Login Failed", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLoginToken(UserLoginToken token){
        if (!new File(getApplicationContext().getFilesDir(), "login.txt").exists()) {
            FileOutputStream fout = null;
            ObjectOutputStream oos = null;
            try {
                fout = getApplicationContext().openFileOutput("login.txt", Context.MODE_PRIVATE);
                oos = new ObjectOutputStream(fout);
                oos.writeObject(token);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try { if (fout != null) fout.close(); } catch(IOException e) {}
                try { if (oos != null) oos.close(); } catch(IOException e) {}
            }
        }
    }
}
