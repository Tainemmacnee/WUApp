package com.example.Wuapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.Wuapp.model.ReportFormState;
import com.example.Wuapp.model.Team;
import com.example.Wuapp.model.WebLoader;
import com.example.Wuapp.ui.loading.LoadingScreen;
import com.example.Wuapp.ui.reportresult.ReportResultFragment;

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
    private ReportFormState submitted = null;

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
        Future<ReportFormState> ffs = WebLoader.getReportFormState(cookies, reportLink);
        LoadingScreen loadingScreen = new LoadingScreen();
        loadingScreen.load("Loading report form", ffs, this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_view, loadingScreen, "LoadingScreen");
        transaction.commit();
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

    private String getOAuthToken(){
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
        submitted = formState;
        Future<Boolean> submittedFormState = WebLoader.submitFormState(cookies, formState, getOAuthToken());
        LoadingScreen loadingScreen = new LoadingScreen("Submitting Report Form", submittedFormState, this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_view, loadingScreen, "loadingScreen");
        transaction.commit();
    }

    @Override
    public void processResult(Object Result, boolean finished){
        if(finished){
            if(submitted == null){ //first time loading
                formState = (ReportFormState) Result;
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_view, new ReportResultFragment(), "REPORTFRAGMENT");
                transaction.commit();
            } else {
                if(Result instanceof ReportFormState){
                    ReportFormState newFormState = (ReportFormState) Result;
                    System.out.println(submitted.Compare(newFormState));
                    finish();
                } else { //Reload new form state to verify it succeeded
                    LoadingScreen loadingScreen = (LoadingScreen) getSupportFragmentManager().findFragmentByTag("loadingScreen");
                    loadingScreen.load("Verifying Submission", WebLoader.getReportFormState(cookies, reportLink), this);
                }
            }
        }
    }
}