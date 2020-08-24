package com.example.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.firstapp.model.User;
import com.example.firstapp.model.UserLoginToken;
import com.example.firstapp.ui.loading.LoadingScreen;
import com.example.firstapp.ui.login.LoginFragment;
import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class MainActivity extends AppCompatActivity {
    public static final String MESSAGE_COOKIES="LOGINCOOKIE";
    public static final String MESSAGE_LINKS="USERLINKS";

    Button b1,b2;
    EditText ed1,ed2;

    TextView tx1;
    int counter = 3;

    public void login(View view){
        //Collect users login info

        b1 = (Button)findViewById(R.id.button);
        ed1 = (EditText)findViewById(R.id.editText);
        ed2 = (EditText)findViewById(R.id.editText2);

        ArrayList<String> params = new ArrayList<>();
        params.add(ed1.getText().toString());
        params.add(ed2.getText().toString());
        Future<UserLoginToken> flt = User.loginUser(params.get(0), params.get(1));

        Handler handler = new Handler();
        int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(flt.isDone()){
                    try {
                        finishLogin(flt.get());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_view, LoadingScreen.newInstance("Logging In"));
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


}
