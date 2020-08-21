package com.example.firstapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.firstapp.model.Team;

import java.util.List;

public class ReportResultActivity extends AppCompatActivity {

    private String homeTeam = null;
    private String awayTeam = null;
    private String homeTeamImg = null;
    private String awayTeamImg = null;
    private List<Team> eventTeams = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
//        eventName = (String) intent.getSerializableExtra(DisplayUserActivity.MESSAGEEVENTNAME);
//        eventTeams = (List<Team>) intent.getSerializableExtra(DisplayUserActivity.MESSAGEEVENTTEAMS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_result);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Report Result");
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
        inflater.inflate(R.menu.report_result_activity, menu);
        return true;
    }

}