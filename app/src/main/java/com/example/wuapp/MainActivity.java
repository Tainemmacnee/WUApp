package com.example.wuapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.wuapp.data.DataManager;
import com.example.wuapp.model.User;
import com.example.wuapp.model.UserLoginToken;
import com.example.wuapp.model.WebLoader;
import com.example.wuapp.ui.events.EventsFragment;
import com.example.wuapp.ui.games.GameTabsFragment;
import com.example.wuapp.ui.games.GamesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * This is the activity that starts when the application is run. It it used to process the users login
 * information
 */
public class MainActivity extends AppCompatActivity{
    public static final String MESSAGE_COOKIES="LOGINCOOKIE";
    public static final String MESSAGE_LOGINTOKEN = "LOGINTOKEN";

    BottomNavigationView navigationView;

    DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        this.dataManager = new DataManager((UserLoginToken) intent.getExtras().getSerializable(MainActivity.MESSAGE_LOGINTOKEN));

        loadFragment(new EventsFragment());

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
                }
                return loadFragment(mfragment);
            }
        });
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


}
