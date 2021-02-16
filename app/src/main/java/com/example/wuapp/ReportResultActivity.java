package com.example.wuapp;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wuapp.data.DataManager;
import com.example.wuapp.data.DataReceiver;
import com.example.wuapp.databinding.ActivityReportResultBinding;
import com.example.wuapp.databinding.ReportResultMvpBoxBinding;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.ReportFormState;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
        binding.team1Name.setText(game.getHomeTeamName());
        binding.team2Name.setText(game.getAwayTeamName());
        Picasso.get().load(game.getHomeTeamImg()).into(binding.team1Image);
        Picasso.get().load(game.getAwayTeamImg()).into(binding.team2Image);

        binding.femaleMvpbox.removeAllViews();
        binding.maleMvpbox.removeAllViews();

        int maleMVPCount = 1, femaleMVPCount = 1;
        for(ReportFormState.spinnerState state : formState.femaleMVPSpinners){
            createMVPBox(binding.femaleMvpbox, "Female MVP #"+femaleMVPCount++, state);
            System.out.println("Female MVP #"+femaleMVPCount);
        }
        for(ReportFormState.spinnerState state : formState.maleMVPSpinners){
            createMVPBox(binding.maleMvpbox, "Male MVP #"+maleMVPCount++, state);
            System.out.println("Male MVP #"+maleMVPCount);
        }

        setupSpinner(binding.reportResultHomeScore, formState.homeScoreSpinner);
        setupSpinner(binding.reportResultAwayScore, formState.awayScoreSpinner);
        setupSpinner(binding.spinnerRules, formState.RKUSpinner);
        setupSpinner(binding.spinnerFouls, formState.FBCSpinner);
        setupSpinner(binding.spinnerFair, formState.FMSpinner);
        setupSpinner(binding.spinnerPositiveAttitude, formState.PASSpinner);
        setupSpinner(binding.spinnerCommunication, formState.COMSpinner);

        binding.reportResultComments.setText(formState.comments);

    }

    private void createMVPBox(ViewGroup parent, String title, ReportFormState.spinnerState state){
        ReportResultMvpBoxBinding mvpBoxBinding = ReportResultMvpBoxBinding.inflate(getLayoutInflater());
        mvpBoxBinding.mvpTitle.setText(title);
        setupSpinner(mvpBoxBinding.mvpSpinner, state);
        parent.addView(mvpBoxBinding.getRoot());
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