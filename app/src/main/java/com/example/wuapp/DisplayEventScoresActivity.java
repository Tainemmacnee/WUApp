package com.example.wuapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import com.example.wuapp.data.DataManager;
import com.example.wuapp.data.DataReceiver;
import com.example.wuapp.databinding.EventStandingBinding;
import com.example.wuapp.model.WebLoader;
import com.example.wuapp.ui.loading.LoadingScreen;
import com.example.wuapp.ui.events.scores.scoresFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An Activity to load and display the standings for an event
 * @author Taine Macnee
 */
public class DisplayEventScoresActivity extends AppCompatActivity implements DataReceiver {

    private String standingsLink;
    private List<Map<String, String>> standings;
    DataManager dataManager;

    public static final String MESSAGESCORESLINK = "messagescoreslink";

    public List<Map<String, String>> getStandings() {
        return standings;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retrieve required info from intent
        Intent intent = getIntent();
        standingsLink = (String) intent.getSerializableExtra(DisplayEventScoresActivity.MESSAGESCORESLINK);
        dataManager = intent.getParcelableExtra(MainActivity.MESSAGE_DATAMANAGER);


        dataManager.makeRequest(this, DataManager.REQUEST_STANDINGS, standingsLink);

        //setup activity display
        setContentView(R.layout.activity_display_event_standings);

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

    @Override
    public <T> void receiveData(ArrayList<T> results) {
        if(results.isEmpty()) { return; }
        if(results.get(0) == null){
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                    .setTitle("ERROR")
                    .setMessage("Unable to load standings: This is probably because they are unavailable for this event at this time.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        } else {
            findViewById(R.id.loading_view).setVisibility(View.GONE); //Hide loading animation

            standings = (List<Map<String, String>>) results.get(0);
            loadTable();
        }
    }
}