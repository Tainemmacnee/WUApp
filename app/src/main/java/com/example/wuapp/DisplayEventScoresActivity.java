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

import com.example.wuapp.model.WebLoader;
import com.example.wuapp.ui.loading.LoadingScreen;
import com.example.wuapp.ui.events.scores.scoresFragment;

import java.util.List;
import java.util.Map;

/**
 * An Activity to load and display the standings for an event
 * @author Taine Macnee
 */
public class DisplayEventScoresActivity extends AppCompatActivity implements LoadingScreen.loadableActivity {

    private String standingsLink;
    private Map<String, String> cookies;
    private List<Map<String, String>> standings;

    public static final String MESSAGESCORESLINK = "messagescoreslink";

    public List<Map<String, String>> getStandings() {
        return standings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retrieve required info from intent
        Intent intent = getIntent();
        standingsLink = (String) intent.getSerializableExtra(DisplayEventScoresActivity.MESSAGESCORESLINK);
        cookies = (Map<String, String>) intent.getSerializableExtra(MainActivity.MESSAGE_COOKIES);

        //setup activity display
        setContentView(R.layout.activity_display_event_standings);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Standings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Load the standings
        new LoadingScreen("Loading Standings", WebLoader.getStandings(cookies, standingsLink), this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report_result_activity, menu);
        return true;
    }

    @Override
    /**
     * This function is called when the loading finishes.
     * @param result the results returned when the loading finished
     * @param finished a boolean showing if there are more things to finish loading or not
     */
    public void processResult(Object result, boolean finished) {
        if(result == null){ //loading failed for some reason.
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
        }
        if(finished){ //implied result is not null
            standings = (List<Map<String, String>>) result;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_view, new scoresFragment());
            transaction.commit();
        }
    }
}