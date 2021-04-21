package com.example.wuapp.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.wuapp.R;
import com.example.wuapp.datamanagers.DataReceiver;
import com.example.wuapp.datamanagers.EventsManager;
import com.example.wuapp.datamanagers.GamesManager;
import com.example.wuapp.datamanagers.OAuthManager;
import com.example.wuapp.datamanagers.ReportFormManager;
import com.example.wuapp.datamanagers.StandingsManager;
import com.example.wuapp.exceptions.InvalidLinkException;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.UserLoginToken;
import com.example.wuapp.activities.displaygame.DisplayGameActivity;
import com.example.wuapp.activities.displaymap.DisplayMapActivity;
import com.example.wuapp.activities.login.LoginActivity;
import com.example.wuapp.activities.reportresult.ReportResultActivity;
import com.example.wuapp.activities.main.events.EventsFragment;
import com.example.wuapp.activities.main.games.GameTabsFragment;
import com.example.wuapp.activities.main.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.net.UnknownHostException;
import java.text.ParseException;

/**
 * This is the activity that starts after a user has been logged in
 */
public class MainActivity extends AppCompatActivity {
    public static final String MESSAGE_EVENTTEAMS = "EVENTTEAMS";

    Fragment currentFragment;
    BottomNavigationView navigationView;
    UserLoginToken loginToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        loginToken = (UserLoginToken) intent.getExtras().getSerializable(getString(R.string.MESSAGE_LOGINTOKEN));

        //setup data managers
        EventsManager.initialise(loginToken, getApplicationContext());
        GamesManager.initialise(loginToken);
        StandingsManager.initialise(loginToken);
        ReportFormManager.initialise(loginToken);
        OAuthManager.initialise(loginToken);

        //setup navigation
        navigationView=(BottomNavigationView)findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.games:
                        currentFragment = new GameTabsFragment();
                        break;
                    case R.id.events:
                        currentFragment = new EventsFragment();
                        EventsManager.getInstance().requestData(new DataReceiver.Request((DataReceiver) currentFragment, null));
                        break;
                    case R.id.settings:
                        currentFragment = new SettingsFragment();
                        break;
                }
                return loadFragment(currentFragment);
            }
        });
        //this is used to force the navigation to load the relevant data
        navigationView.setSelectedItemId(R.id.games);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GamesManager.getInstance().reload();
        navigationView.setSelectedItemId(navigationView.getSelectedItemId());
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    /**
     * Used to start the activity for reporting a games scores/spirit
     * @param view needed to be able to bind function to button. usually null
     * @param game the game to be reported for
     */
    public void reportGame(View view, Game game){
        Intent intent = new Intent(this, ReportResultActivity.class);
        intent.putExtra(getString(R.string.MESSAGE_GAME), game);
        startActivity(intent);
    }

    /**
     * Used to start the activity for viewing the map of a games location
     * @param view needed to be able to bind function to button. usually null
     * @param game the game to display the map for
     */
    public void viewGameMap(View view, Game game){
        Intent intent = new Intent(this, DisplayMapActivity.class);
        intent.putExtra(getString(R.string.MESSAGE_GAME), game);
        startActivity(intent);
    }

    /**
     * Used to reload all data that may have changed while the app was running.
     * @param view needed to be able to bind function to button. usually null
     */
    public void forceReloadAll(View view){
        EventsManager.getInstance().reload();
        GamesManager.getInstance().reload();
        OAuthManager.getInstance().reload();
        navigationView.setSelectedItemId(navigationView.getSelectedItemId());
    }

    /**
     * Used to reload the game (and oauthtoken) data. OAuthToken is reloaded
     * because if the games have failed to load then probably so has the token.
     * @param view needed to be able to bind function to button. usually null
     */
    public void reloadGames(View view){
        GamesManager.getInstance().reload();
        OAuthManager.getInstance().reload();
        navigationView.setSelectedItemId(navigationView.getSelectedItemId());
    }

    /**
     * Used to reload the event (and oauthtoken) data. OAuthToken is reloaded
     * because if the games have failed to load then probably so has the token.
     * @param view needed to be able to bind function to button. usually null
     */
    public void reloadEvents(View view){
        EventsManager.getInstance().reload();
        OAuthManager.getInstance().reload();
        navigationView.setSelectedItemId(navigationView.getSelectedItemId());
    }

    /**
     * This function is used to log the user out
     * @param view needed to be able to bind function to button. usually null
     */
    public void logout(View view){
        File file = new File(getApplicationContext().getFilesDir(), "login.txt");
        if (file.exists()) {
            file.delete();
        }
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /***
     * Used to start the activity to display more info about a game.
     * @param game the game that should be displayed
     */
    public void viewGame(Game game) {
        Intent intent = new Intent(this, DisplayGameActivity.class);
        intent.putExtra(getString(R.string.MESSAGE_GAME), game);
        startActivity(intent);
    }
}
