package com.example.firstapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.firstapp.model.User;
import com.example.firstapp.model.UserLoginToken;
import com.example.firstapp.ui.loading.LoadingScreen;
import com.example.firstapp.ui.login.LoginFragment;
import com.example.firstapp.ui.scores.scoresFragment;
import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class MainActivity extends AppCompatActivity implements LoadingScreen.loadableActivity{
    public static final String MESSAGE_COOKIES="LOGINCOOKIE";
    public static final String MESSAGE_LINKS="USERLINKS";

    Button b1,b2;
    EditText ed1,ed2;

    TextView tx1;
    int counter = 3;

    public void newlogin(View view){
        //Collect users login info

        b1 = (Button)findViewById(R.id.button);
        ed1 = (EditText)findViewById(R.id.editText);
        ed2 = (EditText)findViewById(R.id.editText2);

        ArrayList<String> params = new ArrayList<>();
        params.add(ed1.getText().toString());
        params.add(ed2.getText().toString());
        Future<UserLoginToken> flt = User.loginUser(params.get(0), params.get(1));

        LoadingScreen loadingScreen = new LoadingScreen();
        loadingScreen.load("Logging In", flt, this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_view, loadingScreen);
        transaction.commit();
    }

    public void login(HashMap<String, String> cookies){
        Future<UserLoginToken> flt = User.loginUser(cookies);

        LoadingScreen loadingScreen = new LoadingScreen();
        loadingScreen.load("Loading User Details", flt, this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_view, loadingScreen);
        transaction.commit();
    }

    private void finishLogin(UserLoginToken lt){
        if(lt==null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_view, new LoginFragment());
            transaction.commit();
            Snackbar.make(findViewById(R.id.login_layout), "Login Failed", Snackbar.LENGTH_SHORT).show();
            System.out.println("LOGIN FAILED");
        } else {
            //save login

                File file = new File(getApplicationContext().getFilesDir(), "login.txt");
                if (!file.exists()) {
                    try {
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("login.txt", Context.MODE_PRIVATE));
                        String output = String.format("tsid:%s", lt.getCookies().get("tsid"));
                        outputStreamWriter.write(output);
                        outputStreamWriter.close();
                    } catch (IOException e) {
                        Log.e("Exception", "File write failed: " + e.toString());
                    }
                }

            Intent intent = new Intent(this, DisplayUserActivity.class);
            intent.putExtra(MESSAGE_COOKIES, lt.getCookies());
            intent.putExtra(MESSAGE_LINKS, lt.getLinks());
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_view, new LoginFragment());
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void onStart()
    {
        super.onStart();
        String result = "";
        try {
            InputStream inputStream = getApplicationContext().openFileInput("login.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                result = bufferedReader.readLine();

                inputStream.close();
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(result.length() != 0){
            System.out.println("LOGIN FOUND");
            HashMap cookies = new HashMap<String, String>();
            cookies.put("tsid", result.split(":")[1]);
            login(cookies);
            return;
        }
        System.out.println("NO LOGIN FOUND");
    }

    @Override
    public void processResult(Object Result, boolean finished) {
        if(finished){
            finishLogin((UserLoginToken) Result);
        }
    }
}
