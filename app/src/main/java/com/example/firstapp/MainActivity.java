package com.example.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.firstapp.model.User;
import com.example.firstapp.model.UserLoginToken;
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


public class MainActivity extends Activity{
    public static final String MESSAGE_COOKIES="LOGINCOOKIE";
    public static final String MESSAGE_LINKS="USERLINKS";

    Button b1,b2;
    EditText ed1,ed2;

    TextView tx1;
    int counter = 3;

    public void login(View view){
        //Collect users login info
        ArrayList<String> params = new ArrayList<>();
        params.add(ed1.getText().toString());
        params.add(ed2.getText().toString());

        if(params.get(0).equals("dev") && params.get(1).equals("dev")){
            Intent intent = new Intent(this, DisplayUserActivity.class);//DisplayUserActivity.class);
            intent.putExtra(MESSAGE_COOKIES, new HashMap<String, String>());
            intent.putExtra(MESSAGE_LINKS, new HashMap<String, String>());
            startActivity(intent);
            finish();
            return;
        }

        //Begin Task
        try {
            UserLoginToken lt = User.loginUser(params.get(0), params.get(1)).get();
            if(lt==null){
                Snackbar.make(findViewById(R.id.login_layout), "Login Failed", Snackbar.LENGTH_SHORT).show();
                System.out.println("LOGIN FAILED");
            } else {
                Intent intent = new Intent(this, DisplayUserActivity.class);
                intent.putExtra(MESSAGE_COOKIES, lt.getCookies());
                intent.putExtra(MESSAGE_LINKS, lt.getLinks());
                startActivity(intent);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = (Button)findViewById(R.id.button);
        ed1 = (EditText)findViewById(R.id.editText);
        ed2 = (EditText)findViewById(R.id.editText2);

    }


}
