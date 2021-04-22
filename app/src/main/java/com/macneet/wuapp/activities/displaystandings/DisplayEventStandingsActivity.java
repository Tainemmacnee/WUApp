package com.macneet.wuapp.activities.displaystandings;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.macneet.wuapp.R;
import com.macneet.wuapp.datamanagers.DataReceiver;
import com.macneet.wuapp.datamanagers.StandingsManager;
import com.macneet.wuapp.databinding.ActivityDisplayEventStandingsBinding;
import com.macneet.wuapp.databinding.EventStandingBinding;
import com.macneet.wuapp.exceptions.ElementNotFoundException;
import com.macneet.wuapp.exceptions.InvalidLinkException;
import com.squareup.picasso.Picasso;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * An Activity to load and display the standings for an event
 * @author Taine Macnee
 */
public class DisplayEventStandingsActivity extends AppCompatActivity implements DataReceiver {

    private String standingsLink;
    private List<Map<String, String>> standings;
    ActivityDisplayEventStandingsBinding binding;

    public static final String MESSAGESCORESLINK = "messagescoreslink";

    public List<Map<String, String>> getStandings() {
        return standings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retrieve required info from intent
        Intent intent = getIntent();
        standingsLink = (String) intent.getSerializableExtra(DisplayEventStandingsActivity.MESSAGESCORESLINK);

        StandingsManager.getInstance().requestData(new Request(this, StandingsManager.REQUEST_STANDINGS, standingsLink));

        //setup activity display
        //setContentView(R.layout.activity_display_event_standings);
        binding = ActivityDisplayEventStandingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    public void exit(View view){
        finish();
    }

    private void loadTable(){
        ViewGroup view = findViewById(R.id.scores_box);
        for(Map<String, String> team : standings){
            EventStandingBinding binding = EventStandingBinding.inflate(getLayoutInflater());
            binding.teamName.setText(team.get("name"));
            Picasso.get().load(team.get("image")).into(binding.eventTeamImage);
            binding.record.setText(team.get("record"));
            binding.spirit.setText(team.get("spirit"));
            binding.pointDiff.setText(team.get("pointDiff"));
            view.addView(binding.getRoot());
        }
    }

    public void loadErrorMessage(String message){
        binding.errorView.getRoot().setVisibility(View.VISIBLE);
        binding.errorView.errorMessage.setText(message);
    }

    public void loadNoStandingsMessage(){
        binding.infoMessage2.setVisibility(View.VISIBLE);
    }

    @Override
    public <T> void receiveResponse(Response<T> response) {
        binding.loadingView.getRoot().setVisibility(View.GONE);
        if(response.exception == null){
            if(response.results.isEmpty()) {
                loadErrorMessage("Standings are unavailable at this time");
            } else {
                standings = (List<Map<String, String>>) response.results;
                loadTable();
            }
        } else {
            try{
                throw response.exception;
            } catch (UnknownHostException e){
                loadErrorMessage("We couldn't connect to: wds.usetopscore.com \n please check you are connected to the internet");
            } catch (ElementNotFoundException e) {
                loadNoStandingsMessage();
            }catch (InvalidLinkException e) {
                loadErrorMessage("We couldn't find that information \n has the wds website changed?");
            } catch (ParseException e) {
                loadErrorMessage("We encountered a problem while processing this information \n has the wds website changed?");
            } catch (Throwable throwable) {
                loadErrorMessage(response.exception.getMessage());
            }
        }
    }
}