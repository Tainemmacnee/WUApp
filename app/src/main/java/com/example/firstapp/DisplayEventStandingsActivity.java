package com.example.firstapp;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.firstapp.model.ReportFormState;
import com.example.firstapp.model.WebLoader;
import com.example.firstapp.ui.loading.LoadingScreen;
import com.example.firstapp.ui.scores.scoresFragment;
import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DisplayEventStandingsActivity extends AppCompatActivity implements LoadingScreen.loadableActivity {

    private String standingsLink;
    private Map<String, String> cookies;
    private List<Map<String, String>> standings;

    public static final String MESSAGESTANDINGSLINK = "messagestandingslink";

    public List<Map<String, String>> getStandings() {
        return standings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        standingsLink = (String) intent.getSerializableExtra(DisplayEventStandingsActivity.MESSAGESTANDINGSLINK);
        cookies = (Map<String, String>) intent.getSerializableExtra(MainActivity.MESSAGE_COOKIES);

        setContentView(R.layout.activity_display_event_standings);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Standings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LoadingScreen loadingScreen = new LoadingScreen();
        try {
            loadingScreen.load("Loading Standings", WebLoader.getStandings(cookies, standingsLink), this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_view, loadingScreen);
        transaction.commit();
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

//    private static Future<List<Map<String, String>>> getStandings(Map<String, String> cookies, String link) throws Exception{
//        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        return executor.submit(() -> {
//            ArrayList<Map<String, String>> output = new ArrayList<>();
//                Connection.Response response = Jsoup.connect(link+"/standings")
//                        .method(Connection.Method.GET)
//                        .userAgent(USER_AGENT)
//                        .cookies(cookies)
//                        .execute();
//
//                Element doc = response.parse().getElementsByClass("striped-blocks spacer1").first();
//                if(doc.children() == null) { throw new Exception(); }
//                for(Element team : doc.children()){
//                    HashMap<String, String> info = new HashMap<>();
//                    Element img = team.getElementsByTag("img").first();
//                    info.put("image", img.attr("src"));
//
//                    Element name = team.getElementsByTag("a").first();
//                    info.put("name", name.text());
//
//                    Element record = team.getElementsByClass("plain-link plain-link").first();
//                    info.put("record", record.text());
//
//                    Element spirit = team.getElementsByClass("plain-link plain-link").last();
//                    info.put("spirit", spirit.text());
//
//                    Element pointDiff = team.getElementsByClass("row-fluid-always").last().child(0);
//                    info.put("pointDiff", pointDiff.text());
//                    output.add(info);
//                }
//
//            return output;
//        });
//    }

    @Override
    public void processResult(Object result, boolean finished) {
        if(result == null){
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
        if(finished){
            standings = (List<Map<String, String>>) result;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_view, new scoresFragment());
            transaction.commit();
        }
    }
}