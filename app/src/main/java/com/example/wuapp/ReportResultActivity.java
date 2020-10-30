package com.example.wuapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.wuapp.model.ReportFormState;
import com.example.wuapp.model.Team;
import com.example.wuapp.model.WebLoader;
import com.example.wuapp.ui.loading.LoadingScreen;
import com.example.wuapp.ui.reportresult.SubmissionFragment;
import com.example.wuapp.ui.reportresult.ReportResultFragment;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * An Activity to load the report form and submit any changes
 */
public class ReportResultActivity extends AppCompatActivity implements LoadingScreen.loadableActivity {

    private Team homeTeam;
    private Team awayTeam;
    private String userName;
    private String reportLink;
    private Map<String, String> cookies;
    private ReportFormState formState;
    private Future<String> OAuthToken;

    public Team getHomeTeam() { return homeTeam; }

    public Team getAwayTeam() { return awayTeam; }

    public ReportFormState getReportFormState(){ return this.formState; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        homeTeam = (Team) intent.getSerializableExtra(DisplayUserActivity.MESSAGEHOMETEAM);
        awayTeam = (Team) intent.getSerializableExtra(DisplayUserActivity.MESSAGEAWAYTEAM);
        reportLink = (String) intent.getSerializableExtra(DisplayUserActivity.MESSAGEREPORTLINK);
        cookies = (Map<String, String>) intent.getSerializableExtra(MainActivity.MESSAGE_COOKIES);
        userName = intent.getStringExtra(DisplayUserActivity.MESSAGEUSERNAME);

        //Pre load the OAuthToken so we don't have to wait for it later
        OAuthToken = WebLoader.getOAuthToken(cookies);

        //Setup layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_result);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Report Result");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //load report form state
        new LoadingScreen("Loading report form", WebLoader.getReportFormState(cookies, reportLink), this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report_result_activity, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ReportResultFragment frag = (ReportResultFragment) getSupportFragmentManager().findFragmentByTag("REPORTFRAGMENT");
                if (frag != null && frag.isVisible()) {
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This function works out what team the user is on and returns the opposing team for this game
     * @return the opposing team
     */
    public Team getOtherTeam(){
        if(homeTeam.getFemaleMatchups().contains(userName) || homeTeam.getMaleMatchups().contains(userName)){
            return awayTeam;
        }
        return homeTeam;
    }

    public String getOAuthToken(){
        try {
            return OAuthToken.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void submit(ReportFormState formState){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_view, new SubmissionFragment(formState, OAuthToken, cookies));
        transaction.disallowAddToBackStack();
        transaction.commit();

    }

    @Override
    public void processResult(Object result, boolean finished){
        if(finished){
            formState = (ReportFormState) result;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_view, new ReportResultFragment(), "REPORTFRAGMENT");
            transaction.commit();
        }
    }
}