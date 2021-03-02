package com.example.wuapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.wuapp.model.Game;
import com.example.wuapp.model.WebLoader;
import com.example.wuapp.ui.events.scores.scoresFragment;
import com.example.wuapp.ui.loading.LoadingScreen;

import java.util.List;
import java.util.Map;

/**
 * An Activity to load and display the standings for an event
 * @author Taine Macnee
 */
public class DisplayMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        //retrieve required info from intent
        Intent intent = getIntent();
        Game game = intent.getParcelableExtra(MainActivity.MESSAGE_GAME);

        //setup activity display
        setContentView(R.layout.acvivity_display_map);

        //Load the maps
        if(game.getLocation().contains("liardet")){
            switch (game.getLocation().charAt(game.getLocation().length()-1)){
                case '1':
                    break;
            }
        }
    }


}