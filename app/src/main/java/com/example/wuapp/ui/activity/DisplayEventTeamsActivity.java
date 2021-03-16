package com.example.wuapp.ui.activity;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wuapp.R;
import com.example.wuapp.model.Team;
import com.example.wuapp.ui.fragment.events.teams.EventTeamsAdapter;

import java.util.List;

/**
 * An Activity to load and display the teams for an event
 */
public class DisplayEventTeamsActivity extends AppCompatActivity {

    private String eventName = null;
    private List<Team> eventTeams = null;

    private SearchView searchView;
    private EventTeamsAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //retrieve required info from intent
        Intent intent = getIntent();
        eventName = (String) intent.getSerializableExtra(MainActivity.MESSAGE_EVENTNAME);
        eventTeams = (List<Team>) intent.getSerializableExtra(MainActivity.MESSAGE_EVENTTEAMS);

        //setup activity display
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_event_teams);

        Team[] eventTeams = getEventTeams();

        recyclerView = findViewById(R.id.event_team_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new EventTeamsAdapter(eventTeams);
        recyclerView.setAdapter(mAdapter);
        setmAdapter(mAdapter);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = findViewById(R.id.searchView);
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
    }

    public void exit(View view){
        finish();
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