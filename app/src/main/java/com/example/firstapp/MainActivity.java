package com.example.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class MainActivity extends Activity{
    public static final String EXTRA_MESSAGE="LOGINCOOKIE";

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
            intent.putExtra(EXTRA_MESSAGE, new HashMap<String, String>());
            startActivity(intent);
            return;
        }

        //Begin Task
        AsyncTask task = new MainActivity.LoginTask().execute(params);
        try {
            //Retrieve cookies from result of login attempt
            HashMap<String, String> result = (HashMap<String, String>) task.get();
            if(result==null){
                System.out.println("LOGIN FAILED");
            } else {
                System.out.println(task.get());
                Intent intent = new Intent(this, DisplayUserActivity.class);
                intent.putExtra(EXTRA_MESSAGE, result);
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

    private class LoginTask extends AsyncTask<ArrayList<String>, Void, HashMap<String,String>> {

        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        final String LOGIN_FORM_URL = "https://wds.usetopscore.com";
        final String LOGIN_ACTION_URL = "https://wds.usetopscore.com";

        @Override
        protected HashMap<String,String> doInBackground(ArrayList<String>... params) {
            ArrayList<String> passed = params[0];
            Connection.Response loginActionResponse = null;
            try {
                Connection.Response loginFormResponse = Jsoup.connect(LOGIN_FORM_URL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .execute();
                Element loginForm = loginFormResponse.parse()
                        .getElementsByClass("form-vertical signin exists spacer1").first();
                checkElement("Login Form", loginForm);

                Element emailField = loginForm.getElementsByClass("span3 full initial-focus span3 mailcheck").first();
                checkElement("email Field", emailField);
                emailField.val(passed.get(0));

                Element passwordField = loginForm.getElementById("signin_password");
                checkElement("Password Field", passwordField);
                passwordField.val(passed.get(1));

                FormElement form = (FormElement)loginForm;
                loginActionResponse = form.submit()
                        .cookies(loginFormResponse.cookies())
                        .userAgent(USER_AGENT)
                        .execute();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return (HashMap)loginActionResponse.cookies();
        }

        @Override
        protected void onPostExecute(HashMap<String,String> result) {}

        public void checkElement(String name, Element elem) {
            if (elem == null) {
                System.out.println("NULL");
                throw new RuntimeException("Unable to find " + name);
            }
            System.out.println(elem.className());
        }
    }
}
