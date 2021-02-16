package com.example.wuapp.model;

import android.widget.ArrayAdapter;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * The ReportFormState class is used to store the state of the report form
 */
public class ReportFormState{

    public String comments, homeTeamName, awayTeamName;
    public int homeTeamScore, awayTeamScore, RKU, FBC, FM, PAS, COM;
    public Document doc;
    public spinnerState homeScoreSpinner, awayScoreSpinner, maleMVPSpinner, femaleMVPSpinner, RKUSpinner, FBCSpinner, FMSpinner, PASSpinner, COMSpinner;

    public ReportFormState(Builder builder){

        this.comments = builder.comments;
        this.doc = builder.doc;
        this.homeScoreSpinner = builder.homeScoreSpinner;
        this.awayScoreSpinner = builder.awayScoreSpinner;
        this.maleMVPSpinner = builder.maleMVPSpinner;
        this.femaleMVPSpinner = builder.femaleMVPSpinner;
        this.RKUSpinner = builder.RKUSpinner;
        this.FBCSpinner = builder.FBCSpinner;
        this.FMSpinner = builder.FMSpinner;
        this.PASSpinner = builder.PASSpinner;
        this.COMSpinner = builder.COMSpinner;
        this.homeTeamName = builder.homeTeamName;
        this.awayTeamName = builder.awayTeamName;
    }

    public static class Builder{
        private String comments, homeTeamName, awayTeamName;
        private Document doc;
        spinnerState homeScoreSpinner, awayScoreSpinner, maleMVPSpinner, femaleMVPSpinner, RKUSpinner, FBCSpinner, FMSpinner, PASSpinner, COMSpinner;

        public Builder setHomeScoreSpinner(spinnerState state) { this.homeScoreSpinner = state; return this; }

        public Builder setAwayScoreSpinner(spinnerState state) { this.awayScoreSpinner = state; return this; }

        public Builder setMaleMVPSpinner(spinnerState state) { this.maleMVPSpinner = state; return this; }

        public Builder setFemaleMVPSpinner(spinnerState state) { this.femaleMVPSpinner = state; return this; }

        public Builder setRKUSpinner(spinnerState state) { this.RKUSpinner = state; return this; }

        public Builder setFBCSpinner(spinnerState state) { this.FBCSpinner = state; return this; }

        public Builder setFMSpinner(spinnerState state) { this.FMSpinner = state; return this; }

        public Builder setPASSpinner(spinnerState state) { this.PASSpinner = state; return this; }

        public Builder setCOMSpinner(spinnerState state) { this.COMSpinner = state; return this; }

        public Builder setHomeTeamName(String name) { this.homeTeamName = name; return this; }

        public Builder setAwayTeamName(String name) { this.awayTeamName = name; return this; }

        public Builder setComments(String comments) { this.comments = comments; return this; }

        public Builder setDocument(Document doc) { this.doc = doc; return this; }

        public ReportFormState build(){ return new ReportFormState(this); }
    }

    public static class spinnerState{

        private List<String> spinnerValues;
        private int selectedIndex;

        public spinnerState(List<String> spinnerValues, int selectedIndex){
            this.spinnerValues = spinnerValues;
            this.selectedIndex = selectedIndex;
        }

        public List<String> getSpinnerValues(){
            return spinnerValues;
        }

        public int getSelectedIndex() {
            return selectedIndex;
        }
    }
}
