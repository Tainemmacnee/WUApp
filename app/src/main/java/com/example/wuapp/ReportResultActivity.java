package com.example.wuapp;


import android.app.ProgressDialog;
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
import com.example.wuapp.model.User;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

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
import java.util.concurrent.CompletableFuture;

/**
 * An Activity to load the report form and submit any changes
 */
public class ReportResultActivity extends AppCompatActivity implements DataReceiver {

    ReportFormState reportFormState;
    DataManager dataManager;
    Game game;

    private ActivityReportResultBinding binding;
    private List<ReportResultMvpBoxBinding> MVPBindings = new ArrayList<>();


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
        this.MVPBindings.add(mvpBoxBinding);
    }

    private void setupSpinner(Spinner spinner, ReportFormState.spinnerState state){
        spinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, state.getSpinnerValues()));
        spinner.setSelection(state.getSelectedIndex());
    }

    @Override
    public <T> void receiveData(ArrayList<T> results) {
        if(results != null && results.size() > 0){
            if(results.get(0) instanceof ReportFormState){
                reportFormState = (ReportFormState) results.get(0);
                bindReportFormState((ReportFormState) results.get(0));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void submitReportForm(View view){
        ProgressDialog dialog = ProgressDialog.show(ReportResultActivity.this, "",
                "Reporting Results. Please wait...", true);

        CompletableFuture.allOf( CompletableFuture.supplyAsync(this::submitSpiritAndScores),
                CompletableFuture.supplyAsync(this::submitMVPs))
                .thenAccept(r -> {
                    dialog.dismiss();
                    this.finish();
                });


    }

    private boolean submitSpiritAndScores(){
        try {
            Document reportPage = reportFormState.doc;
            List<Element> selectForms = reportPage.getElementsByTag("select");
            int selectFormIndex = 0;

            setSpinner(selectForms.get(selectFormIndex++), binding.reportResultHomeScore.getSelectedItemPosition());
            setSpinner(selectForms.get(selectFormIndex++), binding.reportResultAwayScore.getSelectedItemPosition());
            setSpinner(selectForms.get(selectFormIndex++), binding.spinnerRules.getSelectedItemPosition());
            setSpinner(selectForms.get(selectFormIndex++), binding.spinnerFouls.getSelectedItemPosition());
            setSpinner(selectForms.get(selectFormIndex++), binding.spinnerFair.getSelectedItemPosition());
            setSpinner(selectForms.get(selectFormIndex++), binding.spinnerPositiveAttitude.getSelectedItemPosition());
            setSpinner(selectForms.get(selectFormIndex++), binding.spinnerCommunication.getSelectedItemPosition());

            String commentsReportLink;
            String comments = binding.reportResultComments.getText().toString();
            if (reportPage.getElementById("game_home_game_report_survey_6_answer") == null) {
                commentsReportLink = "game_away_game_report_survey_6_answer";
            } else {
                commentsReportLink = "game_home_game_report_survey_6_answer";
            }

            FormElement form = (FormElement) reportPage.getElementById("game-report-score-form");
            Connection.Response reportActionResponse = form.submit()
                    .data(commentsReportLink, comments)
                    .cookies(dataManager.getCookies())
                    .userAgent(User.USER_AGENT)
                    .execute();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    //TODO: Error handling
    private boolean submitMVPs(){
        try{
            Document reportPage = reportFormState.doc;
            List<ReportFormState.spinnerState> allMVPs = new ArrayList<>();
            allMVPs.addAll(reportFormState.femaleMVPSpinners);
            allMVPs.addAll(reportFormState.maleMVPSpinners);

            for(int i = 0; i < allMVPs.size(); i++) {
                if (allMVPs.get(i).getSelectedIndex() != MVPBindings.get(i).mvpSpinner.getSelectedItemPosition()) { //MVP has changed
                    Element mvpForm = reportPage.getElementsByClass("form-api live spacer-half keep-popup-open person-award-form").get(i);
                    //get players ID
                    Element selectTag = mvpForm.getElementsByTag("select").first();
                    String playerID = selectTag.child(MVPBindings.get(i).mvpSpinner.getSelectedItemPosition()).attr("value");

                    //get other info needed for reporting mvp
                    Elements inputs = mvpForm.getElementsByTag("input");
                    String gameID = inputs.get(0).attr("value");
                    String teamID = inputs.get(1).attr("value");
                    String rank = inputs.get(2).attr("value");
                    String award = inputs.get(3).attr("value");

                    //build data map
                    Map<String, String> data = new HashMap<>();
                    data.put("person_id", playerID);
                    data.put("game_id", gameID);
                    data.put("team_id", teamID);
                    data.put("rank", rank);
                    data.put("award", award);

                    String link;
                    if (MVPBindings.get(i).mvpSpinner.getSelectedItemPosition() == 0) {
                        data.remove("person_id"); //remove player id so award is given to no one
                        data.put("id", inputs.get(4).attr("value")); //set award id
                        link = "https://wds.usetopscore.com/api/person-award/delete";
                    } else if(allMVPs.get(i).getSelectedIndex() == 0){ //current index != 0 is implied
                        link = "https://wds.usetopscore.com/api/person-award/new";
                    } else {
                        data.put("id", inputs.get(4).attr("value")); //set award id
                        link = "https://wds.usetopscore.com/api/person-award/edit";
                    }

                    Document reportResponse = Jsoup.connect(link)
                            .userAgent(User.USER_AGENT)
                            .ignoreContentType(true)
                            .header("Authorization", "Bearer " + dataManager.getOAuthToken())
                            .data(data)
                            .post();

                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This function finds the id of a player from an html select tag where the players name is an option
     * @param selectTag The encapsulating select tag
     * @param MVPName The name of the player who id were are trying to get
     * @return The id of the player or null if they weren't found
     */
    private String getMVPId(Element selectTag, String MVPName){
        for(Element option : selectTag.children()){
            if(option.text().contains(MVPName)){ //get mvp id from options
                return option.attr("value");
            }
        }
        return null;
    }

    /**
     * This is a helper function for the report function. It is used to set the selected option of
     * the html select tags.
     *
     * @param selectTag The html select tag which needs to have its option set
     * @param index     the index of the option to set the select tag to
     */
    private void setSpinner(Element selectTag, int index){
        //remove selected attribute
        Element selectedOption = selectTag.children().select("[selected]").first();
        if (selectedOption != null) {
            selectedOption.removeAttr("selected");
        }
        //set correct selected option
        selectTag.child(index).attr("selected", "selected");
    }




}