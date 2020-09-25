package com.example.firstapp;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.firstapp.model.ReportFormState;
import com.example.firstapp.model.Team;
import com.example.firstapp.model.User;
import com.example.firstapp.model.WebLoader;
import com.example.firstapp.ui.loading.LoadingScreen;
import com.example.firstapp.ui.reportresult.ReportResultFragment;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        Future<ReportFormState> ffs = WebLoader.getReportFormState(cookies, reportLink);
        LoadingScreen loadingScreen = new LoadingScreen();
        loadingScreen.load("Loading report form", ffs, this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_view, loadingScreen);
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

    public void submit(ReportFormState state){

        Future<Boolean> frMVP = WebLoader.reportMVPs(cookies, state.getAllMvps(), getOAuthToken(), reportLink);
        Future<Boolean> frScore = WebLoader.report(cookies, reportLink, state);

        LoadingScreen loadingScreen = new LoadingScreen();
        loadingScreen.load("Submitting MVPs", frMVP, this);
        loadingScreen.load("Submitting Scores and Spirit", frScore, this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_view, loadingScreen);
        transaction.commit();
    }

    @Override
    public void processResult(Object Result, boolean finished){
        if(finished){
            if(Result instanceof ReportFormState){
                formState = (ReportFormState) Result;
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_view, new ReportResultFragment(), "REPORTFRAGMENT");
                transaction.commit();
            } else {
                try {
                    ReportFormState state = WebLoader.getReportFormState(cookies, reportLink).get();
                    System.out.println(formState.Compare(state));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        }
    }
}