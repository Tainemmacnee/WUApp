package com.macneet.wuapp.activities.reportresult;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.macneet.wuapp.R;
import com.macneet.wuapp.datamanagers.DataReceiver;
import com.macneet.wuapp.datamanagers.DataManager;
import com.macneet.wuapp.datamanagers.OAuthManager;
import com.macneet.wuapp.datamanagers.ReportFormManager;
import com.macneet.wuapp.databinding.ActivityReportResultBinding;
import com.macneet.wuapp.databinding.ReportResultMvpBoxBinding;
import com.macneet.wuapp.exceptions.InvalidLinkException;
import com.macneet.wuapp.model.Game;
import com.macneet.wuapp.model.ReportFormState;
import com.squareup.picasso.Picasso;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import java.net.UnknownHostException;
import java.text.ParseException;
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
    Game game;

    private ActivityReportResultBinding binding;
    private List<ReportResultMvpBoxBinding> MVPBindings = new ArrayList<>();
    private boolean binded = false;
    private String oAuthToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_view);
        binding = ActivityReportResultBinding.inflate(getLayoutInflater());

        Intent intent = getIntent();
        game = intent.getParcelableExtra(getString(R.string.MESSAGE_GAME));

        OAuthManager.getInstance().requestData(new Request(this, OAuthManager.REQUEST_OAUTH_TOKEN));
        ReportFormManager.getInstance().requestData(new Request(this, ReportFormManager.REQUEST_REPORT_FORM, DataManager.HOME_URL + game.getReportLink()));
    }

    public void exit(View view){
        finish();
    }

    public void reload(){
        oAuthToken = null;
        reportFormState = null;

        OAuthManager.getInstance().reload();

        ReportFormManager.getInstance().requestData(new Request(this, ReportFormManager.REQUEST_REPORT_FORM, DataManager.HOME_URL + game.getReportLink()));
        OAuthManager.getInstance().requestData(new Request(this, OAuthManager.REQUEST_OAUTH_TOKEN));
    }

    private void bindReportFormState(ReportFormState formState){
        setContentView(binding.getRoot());
        binded = true;
        binding.team1Name.setText(game.getHomeTeamName());
        binding.team2Name.setText(game.getAwayTeamName());
        Picasso.get().load(game.getHomeTeamImg()).into(binding.team1Image);
        Picasso.get().load(game.getAwayTeamImg()).into(binding.team2Image);

        binding.femaleMvpbox.removeAllViews();
        binding.maleMvpbox.removeAllViews();

        int maleMVPCount = 1, femaleMVPCount = 1;
        for(ReportFormState.spinnerState state : formState.femaleMVPSpinners){
            createMVPBox(binding.femaleMvpbox, "Female MVP #"+femaleMVPCount++, state);
        }
        for(ReportFormState.spinnerState state : formState.maleMVPSpinners){
            createMVPBox(binding.maleMvpbox, "Male MVP #"+maleMVPCount++, state);
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
    public <T> void receiveResponse(Response<T> response) {
        if(response.exception == null && !response.results.isEmpty()){
            if (response.results.get(0) instanceof ReportFormState) { //received report form state

                reportFormState = (ReportFormState) response.results.get(0);

            }
            if(response.results.get(0) instanceof String){ //OAuthToken received
                oAuthToken = (String) response.results.get(0);
            }
            if(!binded && oAuthToken != null && reportFormState != null) {
                bindReportFormState((ReportFormState)reportFormState);
            }
        } else {
            try{
                throw response.exception.getCause();
            } catch (UnknownHostException e){
                loadErrorMessage("Couldn't connect to: wds.usetopscore.com \n please check you are connected to the internet");
            } catch (InvalidLinkException e) {
                loadErrorMessage("Couldn't find that report form \n has the wds website changed?");
            } catch (ParseException e) {
                loadErrorMessage("We encountered a problem while parsing the report form \n has the wds website changed?");
            } catch (Throwable throwable) {
                if(response.initialRequest.request.equals(ReportFormManager.REQUEST_REPORT_FORM)){
                    loadErrorMessage("There was an unknown error while loading the report form");
                } else {
                    loadErrorMessage("There was an unknown error while loading your OAuth2Token");
                }
            }
        }
    }

    private void loadErrorMessage(String message){
        ExceptionDialog.newInstance(message)
                .show(getSupportFragmentManager(), "dialog");
    }

    public void submitReportForm(View view){
        ProgressDialog dialog = ProgressDialog.show(ReportResultActivity.this, "",
                "Reporting Results. Please wait...", true);

        CompletableFuture future = CompletableFuture.supplyAsync(this::submitSpiritAndScores)
                .thenAcceptBoth(CompletableFuture.supplyAsync(this::submitMVPs),
                        (s1, s2) -> {
                            dialog.dismiss();
                            if(s1 && s2){  finish(); }
                            else {
                                String message = "";
                                if(!s1 && !s2){ message = "Failed To Report"; }
                                else if(s1 && !s2) { message = "Failed to report MVP's"; }
                                else if(!s1 && s2) { message = "Failed to report Scores and Spirit"; }
                                    SubmissionExceptionDialog.newInstance(message)
                                .show(getSupportFragmentManager(), "diarog");
                            }
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
            setSpinner(selectForms.get(selectFormIndex), binding.spinnerCommunication.getSelectedItemPosition());

            String commentsReportLink;
            String comments = binding.reportResultComments.getText().toString();



            if (reportPage.getElementById("game_home_game_report_survey_6_answer") == null) {
                commentsReportLink = "game[away_game_report_survey][6][answer]";
            } else {
                commentsReportLink = "game[home_game_report_survey][6][answer]";
            }

            FormElement form = (FormElement) reportPage.getElementById("game-report-score-form");
            Connection.Response response = form.submit()
                    .data(commentsReportLink, comments)
                    .cookies(ReportFormManager.getInstance().getLoginToken().getCookies())
                    .userAgent(DataManager.USER_AGENT)
                    .execute();
            if(response.statusCode() != 200){ return false; }
        } catch (Exception e){
            return false;
        }
        return true;
    }

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

                    Jsoup.connect(link)
                            .userAgent(DataManager.USER_AGENT)
                            .ignoreContentType(true)
                            .header("Authorization", "Bearer " + oAuthToken)
                            .data(data)
                            .ignoreHttpErrors(false)
                            .post();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
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