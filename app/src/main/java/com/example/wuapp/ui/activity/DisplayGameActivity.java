package com.example.wuapp.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wuapp.R;
import com.example.wuapp.data.DataManager;
import com.example.wuapp.data.DataReceiver;
import com.example.wuapp.databinding.ActivityDisplayGameBinding;
import com.example.wuapp.model.Event;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.Team;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.wuapp.ui.activity.MainActivity.MESSAGE_DATAMANAGER;
import static com.example.wuapp.ui.activity.MainActivity.MESSAGE_GAME;

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
        dataManager = intent.getParcelableExtra(MESSAGE_DATAMANAGER);
        game = intent.getParcelableExtra(MESSAGE_GAME);

        binding.team1Name.setText(game.getHomeTeamName());
        binding.team2Name.setText(game.getAwayTeamName());
        binding.gameDatetime.setText(game.getTime() + "   " + game.getDate());
        binding.gameLocation.setText(game.getLocation());
        binding.gameLeague.setText(game.getLeague());
        Picasso.get().load(game.getHomeTeamImg()).into(binding.team1Image);
        Picasso.get().load(game.getAwayTeamImg()).into(binding.team2Image);

        //setup activity display
        setContentView(binding.getRoot());

        makeRequest(dataManager, this, DataManager.REQUEST_EVENTS);
    }

    public void setupButtons(){
        if(game.isReportable()){
            binding.gameReportButton.setBackgroundResource(R.drawable.rounded_background_grey);
            binding.gameReportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ReportResultActivity.class);
                    intent.putExtra(MESSAGE_DATAMANAGER, dataManager);
                    intent.putExtra(MESSAGE_GAME, game);
                    startActivity(intent);
                }
            });
        } else {
            binding.gameReportButton.setBackgroundResource(R.drawable.rounded_background_red);
        }

        if(!game.getLocation().contains("Liardet") &&
                !game.getLocation().contains("MacAlister")){
            binding.gameMapButton.setBackgroundResource(R.drawable.rounded_background_red);
        } else {
            binding.gameMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), DisplayMapActivity.class);
                    intent.putExtra(MESSAGE_DATAMANAGER, dataManager);
                    intent.putExtra(MESSAGE_GAME, game);
                    startActivity(intent);
                }
            });
        }
    }

    public void exit(View view){
        finish();
    }

    private void bindGameTeams(Team team1, Team team2){

        binding.maleMatchupContainer.setVisibility(View.VISIBLE);
        for(String name : team1.getMaleMatchups()){
            TextView newtextview = new TextView(binding.maleMatchupDisplay.getContext());
            newtextview.setText(name);
            newtextview.setGravity(Gravity.CENTER);
            newtextview.setTextColor(getResources().getColor(R.color.colorPrimary));
            binding.maleMatchupDisplay.addView(newtextview);
        }

        binding.femaleMatchupContainer.setVisibility(View.VISIBLE);
        for(String name : team1.getFemaleMatchups()){
            TextView newtextview = new TextView(binding.femaleMatchupDisplay.getContext());
            newtextview.setText(name);
            newtextview.setGravity(Gravity.CENTER);
            newtextview.setTextColor(getResources().getColor(R.color.colorPrimary));
            binding.femaleMatchupDisplay.addView(newtextview);
        }

        binding.team2MaleMatchupContainer.setVisibility(View.VISIBLE);
        for(String name : team2.getMaleMatchups()){
            TextView newtextview = new TextView(binding.team2MaleMatchupDisplay.getContext());
            newtextview.setText(name);
            newtextview.setGravity(Gravity.CENTER);
            newtextview.setTextColor(getResources().getColor(R.color.colorPrimary));
            binding.team2MaleMatchupDisplay.addView(newtextview);
        }

        binding.team2FemaleMatchupContainer.setVisibility(View.VISIBLE);
        for(String name : team2.getFemaleMatchups()){
            TextView newtextview = new TextView(binding.team2FemaleMatchupDisplay.getContext());
            newtextview.setText(name);
            newtextview.setGravity(Gravity.CENTER);
            newtextview.setTextColor(getResources().getColor(R.color.colorPrimary));
            binding.team2FemaleMatchupDisplay.addView(newtextview);
        }

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
                        setupButtons();
                    }
                }
            }
        }
    }
}