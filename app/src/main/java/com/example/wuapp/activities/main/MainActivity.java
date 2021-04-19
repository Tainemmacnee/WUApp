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
 * This is the activity that starts when the application is run. It it used to process the users login
 * information
 */
public class MainActivity extends AppCompatActivity {
    public static final String MESSAGE_COOKIES="LOGINCOOKIE";
    public static final String MESSAGE_LOGINTOKEN = "LOGINTOKEN";
    public static final String MESSAGE_DATAMANAGER = "DATAMANAGER";
    public static final String MESSAGE_GAME = "GAME";
    public static final String MESSAGE_EVENTNAME = "EVENTNAME";
    public static final String MESSAGE_EVENTTEAMS = "EVENTTEAMS";

    Fragment currentFragment;
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        UserLoginToken loginToken = (UserLoginToken) intent.getExtras().getSerializable(MainActivity.MESSAGE_LOGINTOKEN);

        //initialise data managers
        EventsManager.initialise(loginToken, getApplicationContext());
        GamesManager.initialise(loginToken);
        StandingsManager.initialise(loginToken);
        ReportFormManager.initialise(loginToken);
        OAuthManager.initialise(loginToken);

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

    public void reportGame(View view, Game game){
        Intent intent = new Intent(this, ReportResultActivity.class);
        intent.putExtra(MESSAGE_GAME, game);
        startActivity(intent);
    }

    public void viewGameMap(View view, Game game){
        Intent intent = new Intent(this, DisplayMapActivity.class);
        intent.putExtra(MESSAGE_GAME, game);
        startActivity(intent);
    }

    public void forceReloadAll(View view){
        EventsManager.getInstance().reload();
        GamesManager.getInstance().reload();
        OAuthManager.getInstance().reload();
        navigationView.setSelectedItemId(navigationView.getSelectedItemId());
    }

    public void reloadGames(View view){
        GamesManager.getInstance().reload();
        OAuthManager.getInstance().reload();
        navigationView.setSelectedItemId(navigationView.getSelectedItemId());
    }

    public void reloadEvents(View view){
        EventsManager.getInstance().reload();
        OAuthManager.getInstance().reload();
        navigationView.setSelectedItemId(navigationView.getSelectedItemId());
    }

    /**
     * This function is used to log the user out
     * @param view
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


    public void viewGame(Game game) {
        Intent intent = new Intent(this, DisplayGameActivity.class);
        intent.putExtra(MESSAGE_GAME, game);
        startActivity(intent);
    }
}
