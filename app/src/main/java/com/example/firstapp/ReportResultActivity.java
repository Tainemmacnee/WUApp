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

public class ReportResultActivity extends AppCompatActivity {

    private Team homeTeam;
    private Team awayTeam;
    private String userName;
    private String reportLink;
    private Map<String, String> cookies;
    private ReportFormState formState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        homeTeam = (Team) intent.getSerializableExtra(DisplayUserActivity.MESSAGEHOMETEAM);
        awayTeam = (Team) intent.getSerializableExtra(DisplayUserActivity.MESSAGEAWAYTEAM);
        reportLink = (String) intent.getSerializableExtra(DisplayUserActivity.MESSAGEREPORTLINK);
        cookies = (Map<String, String>) intent.getSerializableExtra(MainActivity.MESSAGE_COOKIES);
        userName = (String) intent.getStringExtra(DisplayUserActivity.MESSAGEUSERNAME);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_result);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Report Result");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Future<ReportFormState> ffs = getReportFormState(cookies, reportLink);
        Handler handler = new Handler();
        int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(ffs.isDone()){
                    try {
                        formState = ffs.get();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_view, new ReportResultFragment(), "REPORTFRAGMENT");
                        transaction.commit();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_view, LoadingScreen.newInstance("Loading report form"));
        transaction.commit();
    }

    public Team getOtherTeam(){
        if(homeTeam.getFemaleMatchups().contains(userName) || homeTeam.getMaleMatchups().contains(userName)){
            return awayTeam;
        }
        return homeTeam;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ReportResultFragment frag = (ReportResultFragment) getSupportFragmentManager().findFragmentByTag("REPORTFRAGMENT");
                if (frag != null && frag.isVisible()) {
                    System.out.println("VISIBLE");
                    finish();
                    return true;
                } else {
                    System.out.println("NOT VISIBLE");
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report_result_activity, menu);
        return true;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public String getReportLink() {
        return reportLink;
    }

    public ReportFormState getReportFormState(){
        return this.formState;
    }


    public String getOAuthToken() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        final String WEB_URL = "https://wds.usetopscore.com/u/oauth-key";
        List<String> credentials = executor.submit(() -> {
            List<String> output = new ArrayList<>();
            try {
                Connection.Response loadPageResponse = Jsoup.connect(WEB_URL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .cookies(cookies)
                        .execute();

                Document doc = loadPageResponse.parse();
                Element table = doc.getElementsByClass("table no-border").first();

                for(Element row : table.getElementsByTag("tr")){
                    output.add(row.getElementsByTag("td").first().text());
                }

                output.remove(2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return  output;
        }).get();

       return (String) executor.submit(() -> {


            Document response = Jsoup.connect("https://wds.usetopscore.com/api/oauth/server")
                    .userAgent(USER_AGENT)
                    .data("grant_type", "client_credentials")
                    .data("client_id", credentials.get(0))
                    .data("client_secret", credentials.get(1))
                    .ignoreContentType(true)
                    .post();
            JSONObject result = new JSONObject(response.body().text());
            System.out.println();

            return result.get("access_token");
        }).get();
    }

    public void submit(String homeScore, String awayScore, String RKU, String FBC, String FM, String PAC, String COM, String comments, List<String> mvps){
        try {
            Future<Boolean> frMVP = reportMVPs(mvps);
            Future<Boolean> frScore = report(homeScore, awayScore, RKU, FBC, FM, PAC, COM, comments);

            Handler handler = new Handler();
            int delay = 1000; //milliseconds

            handler.postDelayed(new Runnable(){
                public void run(){
                    //do something
                    if(frMVP.isDone() && frScore.isDone()){
                        finish();
                    } else {
                        handler.postDelayed(this, delay);
                    }
                }
            }, delay);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_view, LoadingScreen.newInstance("Submitting Form"));
            transaction.commit();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Future<Boolean> reportMVPs(List<String> mvps) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        final String WEB_URL = "https://wds.usetopscore.com";
            System.out.println(mvps);
            return executor.submit(() -> {
                String authToken = "";
                try {
                    authToken = getOAuthToken();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {

                    Connection.Response loadPageResponse = Jsoup.connect(WEB_URL + reportLink)
                            .method(Connection.Method.GET)
                            .userAgent(USER_AGENT)
                            .cookies(cookies)
                            .execute();

                    Document doc = loadPageResponse.parse();
                    int count = 0;

                    System.out.println("FIND FORM");
                    for(Element mvpForm : doc.getElementsByClass("form-api live spacer-half keep-popup-open person-award-form")){
                        String playerID = null;
                        String URL = "https://wds.usetopscore.com/api/person-award/edit";
                        Element select = mvpForm.getElementsByTag("select").first();

                      for(Element option : select.children()){
                          if(option.text().contains(mvps.get(count))){
                              playerID = option.attr("value");
                          }
                      }

                        Elements inputs = mvpForm.getElementsByTag("input");
                        String gameID = inputs.get(0).attr("value");
                        String teamID = inputs.get(1).attr("value");
                        String rank = inputs.get(2).attr("value");
                        String award = inputs.get(3).attr("value");

                        Map<String, String> data = new HashMap<>();
                        data.put("person_id", playerID);
                        data.put("game_id", gameID);
                        data.put("team_id", teamID);
                        data.put("rank", rank);
                        data.put("award", award);

                        if(select.child(0).attr("selected").equals("selected")){ //create award if value changed from blank to a name
                            URL = "https://wds.usetopscore.com/api/person-award/new";
                            System.out.println("ADDED");
                        } else {
                            data.put("id", inputs.get(4).attr("value"));
                            if (mvps.get(count).equals("")) { //Delete award if value changed from a name to blank

                                data.remove("person_id");
                                Document response = Jsoup.connect("https://wds.usetopscore.com/api/person-award/delete")
                                        .userAgent(USER_AGENT)
                                        .ignoreContentType(true)
                                        .header("Authorization", "Bearer " + authToken)
                                        .data(data)
                                        .post();
                                System.out.println("DELETED");
                                continue;
                            }
                        }
                        System.out.println("EDITED");
                        Document reportResponse = Jsoup.connect(URL)
                                .userAgent(USER_AGENT)
                                .ignoreContentType(true)
                                .header("Authorization", "Bearer "+authToken)
                                .data(data)
                                .post();


                        System.out.println("REPORTED: "+mvps.get(count));
                        count++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            });
    }

    public Future<Boolean> report(String homeScore, String awayScore, String RKU, String FBC, String FM, String PAC, String COM, String comments) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        final String WEB_URL = "https://wds.usetopscore.com";
        return executor.submit(() -> {
            try {
                Connection.Response loadPageResponse = Jsoup.connect(WEB_URL+reportLink)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .cookies(cookies)
                        .execute();

                Document doc = loadPageResponse.parse();

                Element reportForm = doc.getElementById("game-report-score-form");

                Element gameHomeScore = doc.getElementById("game_home_score");
                setSelection(gameHomeScore, homeScore);

                Element gameAwayScore = doc.getElementById("game_away_score");
                setSelection(gameAwayScore, awayScore);


                Element gameSpirit1 = doc.getElementById("game_home_game_report_survey_1_answer");
                String start = "game_home_";
                if(gameSpirit1 == null){
                    gameSpirit1 = doc.getElementById("game_away_game_report_survey_1_answer");
                    start = "game_away_";
                }
                setSelection(gameSpirit1, RKU);

                Element gameSpirit2 = doc.getElementById(start+"game_report_survey_2_answer");
                setSelection(gameSpirit2, FBC);

                Element gameSpirit3 = doc.getElementById(start+"game_report_survey_3_answer");
                setSelection(gameSpirit3, FM);

                Element gameSpirit4 = doc.getElementById(start+"game_report_survey_4_answer");
                setSelection(gameSpirit4, PAC);

                Element gameSpirit5 = doc.getElementById(start+"game_report_survey_5_answer");
                setSelection(gameSpirit5, COM);

                String commentReport = "";
                if(start.equals("game_home_")){
                    commentReport = "game[home_game_report_survey][6][answer]";
                } else {
                    commentReport = "game[away_game_report_survey][6][answer]";
                }

                FormElement form = (FormElement) reportForm;
                Connection.Response reportActionResponse = form.submit()
                        .data(commentReport, comments)
                        .cookies(cookies)
                        .userAgent(USER_AGENT)
                        .execute();

                System.out.println("DONE! ");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        });
    }

    private void setSelection(Element selectTag, String option){
        //remove selected attribute
        System.out.println("SELECTING FOR: "+selectTag.attr("name"));
        Element selectedOption = selectTag.children().select("[selected]").first();
        if (selectedOption != null) {
            selectedOption.removeAttr("selected");
        }
        //set correct selected option
        for (Element op : selectTag.children()) {
            if (op.text().contains(option)) {
                System.out.println("Selected: "+option);
                op.attr("selected", "selected");
            }
        }
    }

    private Future<ReportFormState> getReportFormState(Map<String, String> cookies, String link){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        final String WEB_URL = "https://wds.usetopscore.com"+link;


        return executor.submit(() -> {
            String homeTeamScore = null;
            String awayTeamScore = null;
            String RKU = null, FBC = null, FM = null, PAS = null, COM = null, comments = null;
            Document doc = null;
            List<String> maleMVPs = new ArrayList<>();
            List<String> femaleMVPs = new ArrayList<>();
            try {
                Connection.Response loadPageResponse = Jsoup.connect(WEB_URL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .cookies(cookies)
                        .execute();

                doc = loadPageResponse.parse();

                Element select = doc.getElementById("game_home_score");
                for(Element option : select.children()){
                    if(option.attr("selected").equals("selected")){
                        homeTeamScore = option.text().trim();
                    }
                }
                select = doc.getElementById("game_away_score");
                for(Element option : select.children()){
                    if(option.attr("selected").equals("selected")){
                        awayTeamScore = option.text().trim();
                    }
                }

                //Check if were reporting for a home or away game
                //ids start with home/away so need to fix
                Element selectRKU = (doc.getElementById("game_home_game_report_survey_1_answer"));
                String start = "game_home_";
                if(selectRKU == null){
                    selectRKU = doc.getElementById("game_away_game_report_survey_1_answer");
                    start = "game_away_";
                }
                for(Element option : selectRKU.children()){
                    if(option.attr("selected").equals("selected")){
                        RKU = option.text().trim();
                    }
                }

                Element selectFBC =  doc.getElementById(start+"game_report_survey_2_answer");
                for(Element option : selectFBC.children()){
                    if(option.attr("selected").equals("selected")){
                        FBC = option.text().trim();
                    }
                }

                Element selectFM =  doc.getElementById(start+"game_report_survey_3_answer");
                for(Element option : selectFM.children()){
                    if(option.attr("selected").equals("selected")){
                        FM = option.text().trim();
                    }
                }

                Element selectPAS =  doc.getElementById(start+"game_report_survey_4_answer");
                for(Element option : selectPAS.children()){
                    if(option.attr("selected").equals("selected")){
                        PAS = option.text();
                        System.out.println("IN: "+PAS);
                    }
                }

                Element selectCOM =  doc.getElementById(start+"game_report_survey_5_answer");
                for(Element option : selectCOM.children()){
                    if(option.attr("selected").equals("selected")){
                        COM = option.text().trim();
                    }
                }

                Element commentsElem =  doc.getElementById(start+"game_report_survey_6_answer");
                comments = commentsElem.text();

                //Collect MVPS
                for(Element mvpForm : doc.getElementsByClass("form-api live spacer-half keep-popup-open person-award-form")) {
                    if (mvpForm.child(3).attr("value").equals("female_mvp")) {
                        for (Element option : mvpForm.getElementsByTag("select").last().children()) {
                            if (option.attr("selected").equals("selected")) {
                                femaleMVPs.add(option.text());
                            }
                        }
                        System.out.println("FEMALE MVP");
                    }
                    if (mvpForm.child(3).attr("value").equals("male_mvp")) {
                        for (Element option : mvpForm.getElementsByTag("select").last().children()) {
                            if (option.attr("selected").equals("selected")) {
                                maleMVPs.add(option.text());
                            }
                            System.out.println("MALE MVP");
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ReportFormState(doc, homeTeamScore, awayTeamScore, RKU, FBC, FM, PAS, COM, comments, maleMVPs, femaleMVPs);
        });
    }
}