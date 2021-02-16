package com.example.wuapp;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.wuapp.data.DataManager;
import com.example.wuapp.data.DataReceiver;
import com.example.wuapp.databinding.ActivityReportResultBinding;
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

    private ActivityReportResultBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportResultBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        dataManager = intent.getParcelableExtra(MainActivity.MESSAGE_DATAMANAGER);
        game = intent.getParcelableExtra(MainActivity.MESSAGE_GAME);

        dataManager.makeRequest(this, DataManager.REQUEST_REPORT_FORM, DataManager.HOME_URL + game.getReportLink());


    }

    private void bindReportFormState(ReportFormState formState){
        binding.reportResultHomeName.setText(formState.homeTeamName);
        binding.reportResultAwayName.setText(formState.awayTeamName);
        setupSpinner(binding.reportResultHomeScore, formState.homeScoreSpinner);
        setupSpinner(binding.reportResultAwayScore, formState.awayScoreSpinner);
        setupSpinner(binding.spinnerRules, formState.RKUSpinner);
        setupSpinner(binding.spinnerFouls, formState.FBCSpinner);
        setupSpinner(binding.spinnerFair, formState.FMSpinner);
        setupSpinner(binding.spinnerPositiveAttitude, formState.PASSpinner);
        setupSpinner(binding.spinnerCommunication, formState.COMSpinner);

        binding.reportResultComments.setText(formState.comments);

    }

    private void setupSpinner(Spinner spinner, ReportFormState.spinnerState state){
        spinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, state.getSpinnerValues()));
        spinner.setSelection(state.getSelectedIndex());
    }

    @Override
    public <T> void receiveData(ArrayList<T> results) {
        if(results != null && results.size() > 0){
            if(results.get(0) instanceof ReportFormState){
                bindReportFormState((ReportFormState) results.get(0));
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