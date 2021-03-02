package com.example.wuapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.wuapp.data.DataManager;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.UserLoginToken;
import com.example.wuapp.ui.events.EventsFragment;
import com.example.wuapp.ui.games.GameTabsFragment;
import com.example.wuapp.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

/**
 * This is the activity that starts when the application is run. It it used to process the users login
 * information
 */
public class MainActivity extends AppCompatActivity{
    public static final String MESSAGE_COOKIES="LOGINCOOKIE";
    public static final String MESSAGE_LOGINTOKEN = "LOGINTOKEN";
    public static final String MESSAGE_DATAMANAGER = "DATAMANAGER";
    public static final String MESSAGE_GAME = "GAME";

    BottomNavigationView navigationView;

    DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        this.dataManager = new DataManager((UserLoginToken) intent.getExtras().getSerializable(MainActivity.MESSAGE_LOGINTOKEN));

        navigationView=(BottomNavigationView)findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment mfragment = null;
                switch (item.getItemId()){
                    case R.id.games:
                        mfragment = new GameTabsFragment();
                        break;
                    case R.id.events:
                        mfragment = new EventsFragment();
                        break;
                    case R.id.settings:
                        mfragment = new SettingsFragment();
                        break;
                }
                return loadFragment(mfragment);
            }
        });
        navigationView.setSelectedItemId(R.id.games);
    }

    public DataManager getDataManager(){
        return this.dataManager;
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
        intent.putExtra(MESSAGE_DATAMANAGER, dataManager);
        intent.putExtra(MESSAGE_GAME, game);
        startActivity(intent);
    }

    public void viewGameMap(View view, Game game){
        Intent intent = new Intent(this, DisplayMapActivity.class);
        intent.putExtra(MESSAGE_DATAMANAGER, dataManager);
        intent.putExtra(MESSAGE_GAME, game);
        startActivity(intent);
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


}
