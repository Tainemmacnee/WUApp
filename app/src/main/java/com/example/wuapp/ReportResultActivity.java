package com.example.wuapp;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.wuapp.data.DataManager;
import com.example.wuapp.data.DataReceiver;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.ReportFormState;
import com.example.wuapp.model.Team;
import com.example.wuapp.model.WebLoader;
import com.example.wuapp.ui.loading.LoadingScreen;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * An Activity to load the report form and submit any changes
 */
public class ReportResultActivity extends AppCompatActivity implements DataReceiver {


    DataManager dataManager;
    Game game;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_result);

        Intent intent = getIntent();
        dataManager = intent.getParcelableExtra(MainActivity.MESSAGE_DATAMANAGER);
        game = intent.getParcelableExtra(MainActivity.MESSAGE_GAME);

        dataManager.downloadReportForm(this, DataManager.HOME_URL + game.getReportLink());


    }

    @Override
    public <T> void receiveData(ArrayList<T> results) {
        if(results != null && results.size() > 0){
            if(results.get(0) instanceof ReportFormState){
                TextView textView = findViewById(R.id.test_text);
                textView.setText(((ReportFormState) results.get(0)).homeTeamName);
            }
        }
    }


//    public void submit(ReportFormState formState){
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        //transaction.replace(R.id.fragment_view, new SubmissionFragment(formState, OAuthToken, cookies));
//        transaction.disallowAddToBackStack();
//        transaction.commit();
//    }
}