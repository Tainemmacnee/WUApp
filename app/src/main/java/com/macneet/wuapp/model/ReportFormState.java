package com.macneet.wuapp.model;

import org.jsoup.nodes.Document;

import java.util.List;

/**
 * The ReportFormState class is used to store the state of the report form
 */
public class ReportFormState{

    public String comments;
    public Document doc;
    public spinnerState homeScoreSpinner, awayScoreSpinner, RKUSpinner, FBCSpinner, FMSpinner, PASSpinner, COMSpinner;
    public List<spinnerState> maleMVPSpinners, femaleMVPSpinners;

    public ReportFormState(Builder builder){
        this.comments = builder.comments;
        this.doc = builder.doc;
        this.homeScoreSpinner = builder.homeScoreSpinner;
        this.awayScoreSpinner = builder.awayScoreSpinner;
        this.maleMVPSpinners = builder.maleMVPSpinners;
        this.femaleMVPSpinners = builder.femaleMVPSpinners;
        this.RKUSpinner = builder.RKUSpinner;
        this.FBCSpinner = builder.FBCSpinner;
        this.FMSpinner = builder.FMSpinner;
        this.PASSpinner = builder.PASSpinner;
        this.COMSpinner = builder.COMSpinner;
    }

    public static class Builder{
        private String comments;
        private Document doc;
        spinnerState homeScoreSpinner, awayScoreSpinner, RKUSpinner, FBCSpinner, FMSpinner, PASSpinner, COMSpinner;
        List<spinnerState> maleMVPSpinners, femaleMVPSpinners;

        public Builder setHomeScoreSpinner(spinnerState state) { this.homeScoreSpinner = state; return this; }

        public Builder setAwayScoreSpinner(spinnerState state) { this.awayScoreSpinner = state; return this; }

        public Builder setMaleMVPSpinners(List<spinnerState> state) { this.maleMVPSpinners = state; return this; }

        public Builder setFemaleMVPSpinners(List<spinnerState> state) { this.femaleMVPSpinners = state; return this; }

        public Builder setRKUSpinner(spinnerState state) { this.RKUSpinner = state; return this; }

        public Builder setFBCSpinner(spinnerState state) { this.FBCSpinner = state; return this; }

        public Builder setFMSpinner(spinnerState state) { this.FMSpinner = state; return this; }

        public Builder setPASSpinner(spinnerState state) { this.PASSpinner = state; return this; }

        public Builder setCOMSpinner(spinnerState state) { this.COMSpinner = state; return this; }

        public Builder setComments(String comments) { this.comments = comments; return this; }

        public Builder setDocument(Document doc) { this.doc = doc; return this; }

        public ReportFormState build(){ return new ReportFormState(this); }
    }

    public static class spinnerState{

        private List<String> spinnerValues;
        private int selectedIndex;
        private boolean locked;

        public spinnerState(List<String> spinnerValues, int selectedIndex, boolean locked){
            this.spinnerValues = spinnerValues;
            this.selectedIndex = selectedIndex;
            this.locked = locked;
        }

        public List<String> getSpinnerValues(){
            return spinnerValues;
        }

        public int getSelectedIndex() {
            return selectedIndex;
        }

        public boolean isLocked() { return locked; }
    }
}
