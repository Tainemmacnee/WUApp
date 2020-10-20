package com.example.Wuapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.Wuapp.model.UserLoginToken;
import com.example.Wuapp.model.WebLoader;
import com.example.Wuapp.ui.loading.LoadingScreen;
import com.example.Wuapp.ui.login.LoginFragment;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Future;

/**
 * This is the activity that starts when the application is run. It it used to process the users login
 * information
 */
public class MainActivity extends AppCompatActivity implements LoadingScreen.loadableActivity{
    public static final String MESSAGE_COOKIES="LOGINCOOKIE";
    public static final String MESSAGE_LOGINTOKEN = "LOGINTOKEN";

    Button b1;
    EditText ed1,ed2;

    /**
     * This function is called when the user presses the login button. it is used to collect the login credentials
     * and attempt to login
     * @param view
     */
    public void login(View view){
        //Collect users login info

        b1 = (Button)findViewById(R.id.button);
        ed1 = (EditText)findViewById(R.id.editText);
        ed2 = (EditText)findViewById(R.id.editText2);

        ArrayList<String> params = new ArrayList<>();
        params.add(ed1.getText().toString());
        params.add(ed2.getText().toString());
        Future<UserLoginToken> flt = WebLoader.loginUser(params.get(0), params.get(1));

        LoadingScreen loadingScreen = new LoadingScreen();
        loadingScreen.load("Logging In", flt, this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_view, loadingScreen);
        transaction.commit();
    }

    /**
     * This function is called when the user has been successfully logged in and is used to save their
     * credentials
     * @param lt
     */
    private void finishLogin(UserLoginToken lt){
            //save login
        File file = new File(getApplicationContext().getFilesDir(), "login.txt");
        if (!file.exists()) {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("login.txt", Context.MODE_PRIVATE));
                BufferedWriter writer = new BufferedWriter(outputStreamWriter);
                String output = String.format("tsid:%s", lt.getCookies().get("tsid"));
                writer.write(output);
                writer.newLine();
                writer.write(lt.getLinks().toString());
                writer.close();
                outputStreamWriter.close();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

        Intent intent = new Intent(this, DisplayUserActivity.class);
        intent.putExtra(MESSAGE_LOGINTOKEN, lt);
        startActivity(intent);
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

    /**
     * This function is used to check if the user already has credentials saved and if so loads them
     */
    public void onStart() {
        super.onStart();
        String cookieString = "";
        String linkString = "";
        HashMap<String,String> links = new HashMap<>();
        HashMap<String,String> cookies = new HashMap<>();
        try {
            InputStream inputStream = getApplicationContext().openFileInput("login.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                cookieString = bufferedReader.readLine();
                linkString = bufferedReader.readLine();

                inputStream.close();
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(cookieString.length() != 0 && linkString.length() != 0){
            linkString = linkString.substring(1, linkString.length()-1);           //remove curly brackets
            String[] keyValuePairs = linkString.split(",");              //split the string to create key-value pairs

            for(String pair : keyValuePairs)                        //iterate over the pairs
            {
                String[] entry = pair.split("=");                   //split the pairs to get key and value
                links.put(entry[0].trim(), entry[1].trim());          //add them to the hashmap and trim whitespaces
            }
            cookies.put("tsid", cookieString.split(":")[1]);
            finishLogin(new UserLoginToken(cookies, links));
        }
    }

    @Override
    /**
     * This function is called when the activity finishes loading something
     * @param result the results returned when the loading finished
     * @param finished a boolean showing if there are more things to finish loading or not
     */
    public void processResult(Object Result, boolean finished) {
        if(finished){
            if(Result == null){ //Login failed
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_view, new LoginFragment());
                transaction.commit();
                Snackbar.make(findViewById(R.id.login_layout), "Login Failed", Snackbar.LENGTH_SHORT).show();
            } else {
                finishLogin((UserLoginToken) Result);
            }
        }
    }
}
