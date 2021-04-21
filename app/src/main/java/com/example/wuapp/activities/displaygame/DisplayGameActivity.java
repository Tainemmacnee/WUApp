package com.example.wuapp.activities.displaygame;


import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wuapp.R;
import com.example.wuapp.datamanagers.DataReceiver;
import com.example.wuapp.datamanagers.EventsManager;
import com.example.wuapp.databinding.ActivityDisplayGameBinding;
import com.example.wuapp.databinding.GameInfoDisplayBinding;
import com.example.wuapp.model.Event;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.Team;
import com.example.wuapp.activities.displaymap.DisplayMapActivity;
import com.example.wuapp.activities.reportresult.ReportResultActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * An Activity to load and display the teams for an event
 */
public class DisplayGameActivity extends AppCompatActivity implements DataReceiver {

    private ActivityDisplayGameBinding binding;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayGameBinding.inflate(getLayoutInflater());

        //retrieve required info from intent
        Intent intent = getIntent();
        game = intent.getParcelableExtra(getString(R.string.MESSAGE_GAME));

        binding.team1Name.setText(game.getHomeTeamName());
        binding.team1Name.setMaxLines(3);
        binding.team2Name.setText(game.getAwayTeamName());
        binding.team2Name.setMaxLines(3);
        binding.gameDatetime.setText(game.getTime() + "   " + game.getDate());
        binding.gameLocation.setText(game.getLocation());
        binding.gameLeague.setText(game.getLeague());
        Picasso.get().load(game.getHomeTeamImg()).into(binding.team1Image);
        Picasso.get().load(game.getAwayTeamImg()).into(binding.team2Image);
        setupButtons();
        if(game.hasScores()) { loadScoreInfo(); }

        //setup activity display
        setContentView(binding.getRoot());
        EventsManager.getInstance().requestData(new Request(this, EventsManager.REQUEST_EVENTS));
    }

    public void loadScoreInfo(){
        GameInfoDisplayBinding gameDisplayBinding = binding.gameInfoDisplay;
        gameDisplayBinding.team1ScoresCard.setVisibility(View.VISIBLE);
        gameDisplayBinding.team1Score.setText(game.getHomeTeamScore());
        gameDisplayBinding.team1Spirit.setText(game.getHomeTeamSpirit());
        gameDisplayBinding.team2ScoresCard.setVisibility(View.VISIBLE);
        gameDisplayBinding.team2Score.setText(game.getAwayTeamScore());
        gameDisplayBinding.team2Spirit.setText(game.getAwayTeamSpirit());
    }

    public void setupButtons(){
        if(game.isReportable()){
            binding.gameReportButton.setEnabled(true);
            binding.gameReportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ReportResultActivity.class);
                    intent.putExtra(getString(R.string.MESSAGE_GAME), game);
                    startActivity(intent);
                }
            });
        } else {
            binding.gameReportButton.setEnabled(false);
        }

        if(game.getLocation().contains("Liardet") ||
                game.getLocation().contains("MacAlister")){
            binding.gameMapButton.setEnabled(true);
            binding.gameMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), DisplayMapActivity.class);
                    intent.putExtra(getString(R.string.MESSAGE_GAME), game);
                    startActivity(intent);
                }
            });
        } else {
            binding.gameMapButton.setEnabled(false);
            binding.gameMapButton.setVisibility(View.GONE);

        }
    }

    public void exit(View view){
        finish();
    }

    private void bindGameTeams(Team team1, Team team2){

        if(team1 == null || team2 == null){
            hideTeamMatchups();
            return;
        }

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

    private void hideTeamMatchups(){
        binding.team1MatchupWrapper.setVisibility(View.GONE);
        binding.team2MatchupWrapper.setVisibility(View.GONE);
        binding.nomatchupText.setVisibility(View.VISIBLE);
    }

    @Override
    public <T> void receiveResponse(Response<T> response) {
        binding.loadingView.getRoot().setVisibility(View.GONE); //Hide loading animation
        if(response.exception == null){
            if (!response.results.isEmpty() && response.results.get(0) instanceof Event) {
                ArrayList<Event> events = (ArrayList<Event>) response.results;

                if(events.stream().anyMatch(event -> event.getName().equals(game.getLeague()))) {
                    for (Event event : events) { //Find the correct event for this league
                        if (event.getName().equals(game.getLeague())) {
                            binding.loadingView.getRoot().setVisibility(View.GONE); //hide loading animation
                            binding.gameViewRoot.setVisibility(View.VISIBLE);
                            bindGameTeams(event.getTeam(game.getHomeTeamName()), event.getTeam(game.getAwayTeamName()));
                        }
                    }
                } else {
                    hideTeamMatchups();
                }
            } else {
                hideTeamMatchups();
            }
        } else {
            hideTeamMatchups();
        }
    }
}