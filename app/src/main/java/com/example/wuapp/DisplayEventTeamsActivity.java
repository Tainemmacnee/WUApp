package com.example.wuapp;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.example.wuapp.model.Team;
import com.example.wuapp.ui.events.teams.EventTeamsAdapter;

import java.util.List;

/**
 * An Activity to load and display the teams for an event
 */
public class DisplayEventTeamsActivity extends AppCompatActivity {

    private String eventName = null;
    private List<Team> eventTeams = null;

    private SearchView searchView;
    private EventTeamsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //retrieve required info from intent
        Intent intent = getIntent();
        eventName = (String) intent.getSerializableExtra(DisplayUserActivity.MESSAGEEVENTNAME);
        eventTeams = (List<Team>) intent.getSerializableExtra(DisplayUserActivity.MESSAGEEVENTTEAMS);

        //setup activity display
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
            case R.id.action_search:
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    /**
     * This function is used to setup the search functionality
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.display_event_teams_activity, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    public Team[] getEventTeams() {
        if(eventTeams != null) {
            return eventTeams.toArray(new Team[eventTeams.size()]);
        }
        return new Team[0];
    }

    public void setmAdapter(EventTeamsAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

}