package com.macneet.wuapp.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.macneet.wuapp.R;
import com.macneet.wuapp.datamanagers.APIGameManager;
import com.macneet.wuapp.datamanagers.DataReceiver;
import com.macneet.wuapp.datamanagers.EventsManager;
import com.macneet.wuapp.datamanagers.OAuthManager;
import com.macneet.wuapp.datamanagers.ReportFormManager;
import com.macneet.wuapp.datamanagers.StandingsManager;
import com.macneet.wuapp.model.Game;
import com.macneet.wuapp.model.UserLoginToken;
import com.macneet.wuapp.activities.displaygame.DisplayGameActivity;
import com.macneet.wuapp.activities.displaymap.DisplayMapActivity;
import com.macneet.wuapp.activities.login.LoginActivity;
import com.macneet.wuapp.activities.reportresult.ReportResultActivity;
import com.macneet.wuapp.activities.main.events.EventsFragment;
import com.macneet.wuapp.activities.main.games.GameTabsFragment;
import com.macneet.wuapp.activities.main.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

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
        EventsManager.initialise(loginToken, this);
        APIGameManager.initialise(loginToken);
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
        intent.putExtra(getString(R.string.MESSAGE_LOGINTOKEN), loginToken);
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
        File file = new File(this.getFilesDir(), "token.txt");
        if(file.exists()) {
            file.delete();
        }
        EventsManager.getInstance().reload();
        APIGameManager.getInstance().reload();
        navigationView.setSelectedItemId(navigationView.getSelectedItemId());
    }

    /**
     * Used to reload the game data.
     * @param view needed to be able to bind function to button. usually null
     */
    public void reloadGames(View view){
        APIGameManager.getInstance().reload();
        navigationView.setSelectedItemId(navigationView.getSelectedItemId());
    }

    /**
     * Used to reload the event
     * @param view needed to be able to bind function to button. usually null
     */
    public void reloadEvents(View view){
        EventsManager.getInstance().reload();
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
