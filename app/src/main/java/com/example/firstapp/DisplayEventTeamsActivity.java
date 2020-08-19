package com.example.firstapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.firstapp.model.Event;
import com.example.firstapp.model.Team;

import java.util.List;

public class DisplayEventTeamsActivity extends AppCompatActivity {

    private String eventName = null;
    private List<Team> eventTeams = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        eventName = (String) intent.getSerializableExtra(DisplayUserActivity.MESSAGEEVENTNAME);
        eventTeams = (List<Team>) intent.getSerializableExtra(DisplayUserActivity.MESSAGEEVENTTEAMS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_event_teams);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle(eventName);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        inflater.inflate(R.menu.display_event_teams_activity, menu);
        return true;
    }

    public Team[] getEventTeams() {
        return eventTeams.toArray(new Team[eventTeams.size()]);
    }
}