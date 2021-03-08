package com.example.wuapp;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.example.wuapp.data.DataManager;
import com.example.wuapp.data.DataReceiver;
import com.example.wuapp.databinding.ActivityDisplayGameBinding;
import com.example.wuapp.model.Event;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.Team;
import com.example.wuapp.ui.events.teams.EventTeamsAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * An Activity to load and display the teams for an event
 */
public class DisplayGameActivity extends AppCompatActivity implements DataReceiver {

    private ActivityDisplayGameBinding binding;
    private DataManager dataManager;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayGameBinding.inflate(getLayoutInflater());

        //retrieve required info from intent
        Intent intent = getIntent();
        dataManager = intent.getParcelableExtra(MainActivity.MESSAGE_DATAMANAGER);
        game = intent.getParcelableExtra(MainActivity.MESSAGE_GAME);

        binding.team1Name.setText(game.getHomeTeamName());
        binding.team2Name.setText(game.getAwayTeamName());
        binding.gameDate.setText(game.getDate());
        binding.gameTime.setText(game.getTime());
        binding.gameLocation.setText(game.getLocation());
        Picasso.get().load(game.getHomeTeamImg()).into(binding.team1Image);
        Picasso.get().load(game.getAwayTeamImg()).into(binding.team2Image);

        //setup activity display
        setContentView(binding.getRoot());

        makeRequest(dataManager, this, DataManager.REQUEST_EVENTS);
    }

    private void bindGameTeams(Team team1, Team team2){

        binding.maleMatchupContainer.setVisibility(View.VISIBLE);
        for(String name : team1.getMaleMatchups()){
            TextView newtextview = new TextView(binding.maleMatchupDisplay.getContext());
            newtextview.setText(name);
            newtextview.setTextColor(getResources().getColor(R.color.colorPrimary));
            binding.maleMatchupDisplay.addView(newtextview);
        }

        binding.femaleMatchupContainer.setVisibility(View.VISIBLE);
        for(String name : team1.getFemaleMatchups()){
            TextView newtextview = new TextView(binding.femaleMatchupDisplay.getContext());
            newtextview.setText(name);
            newtextview.setTextColor(getResources().getColor(R.color.colorPrimary));
            binding.femaleMatchupDisplay.addView(newtextview);
        }

        binding.team2MaleMatchupContainer.setVisibility(View.VISIBLE);
        for(String name : team2.getMaleMatchups()){
            TextView newtextview = new TextView(binding.team2MaleMatchupDisplay.getContext());
            newtextview.setText(name);
            newtextview.setTextColor(getResources().getColor(R.color.colorPrimary));
            binding.team2MaleMatchupDisplay.addView(newtextview);
        }

        binding.team2FemaleMatchupContainer.setVisibility(View.VISIBLE);
        for(String name : team2.getFemaleMatchups()){
            TextView newtextview = new TextView(binding.team2FemaleMatchupDisplay.getContext());
            newtextview.setText(name);
            newtextview.setTextColor(getResources().getColor(R.color.colorPrimary));
            binding.team2FemaleMatchupDisplay.addView(newtextview);
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public <T> void receiveData(ArrayList<T> results) {
        if(results != null && !results.isEmpty()){
            if(results.get(0) instanceof Event){
                ArrayList<Event> events = (ArrayList<Event>) results;

                for(Event event : events){ //Find the correct event for this league
                    if(event.getName().equals(game.getLeague())){
                        binding.loadingView.getRoot().setVisibility(View.GONE); //hide loading animation
                        binding.gameViewRoot.setVisibility(View.VISIBLE);
                        bindGameTeams(event.getTeam(game.getHomeTeamName()), event.getTeam(game.getAwayTeamName()));
                    }
                }
            }
        }
    }
}