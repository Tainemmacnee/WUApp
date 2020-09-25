package com.example.firstapp.model;

import android.widget.LinearLayout;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * The ReportFormState class is used to store the state of the report form
 */
public class ReportFormState{

    public String homeTeamScore, awayTeamScore, RKU, FBC, FM, PAS, COM, comments;
    public List<String> maleMVPs, femaleMVPs;
    public Document doc;

    public ReportFormState(Builder builder){
        this.RKU = builder.RKU;
        this.FBC = builder.FBC;
        this.FM = builder.FM;
        this.PAS = builder.PAS;
        this.COM = builder.COM;
        this.homeTeamScore = builder.homeTeamScore;
        this.awayTeamScore = builder.awayTeamScore;
        this.comments = builder.comments;
        this.doc = builder.doc;
        this.femaleMVPs = builder.femaleMVPs;
        this.maleMVPs = builder.maleMVPs;
    }

    public List<String> getAllMvps() {
        List<String> mvps = new ArrayList<>();
        mvps.addAll(maleMVPs);
        mvps.addAll(femaleMVPs);
        return mvps;
    }

    public boolean Compare(ReportFormState state){
        return(homeTeamScore.equals(state.homeTeamScore) && awayTeamScore.equals(state.awayTeamScore)
        && maleMVPs.equals(state.maleMVPs) && femaleMVPs.equals(state.femaleMVPs) && comments.equals(state.comments)
        && RKU.equals(state.RKU) && FBC.equals(state.FBC) && FM.equals(state.FM) && PAS.equals(state.PAS)
        && COM.equals(state.COM));
    }

    public static class Builder{
        private String homeTeamScore, awayTeamScore, RKU, FBC, FM, PAS, COM, comments;
        private List<String> maleMVPs, femaleMVPs;
        private Document doc;

        public Builder setHomeTeamScore(String homeTeamScore) { this.homeTeamScore = homeTeamScore; return this; }

        public Builder setAwayTeamScore(String awayTeamScore) { this.awayTeamScore = awayTeamScore; return this; }

        public Builder setRKU(String RKU) { this.RKU = RKU; return this; }

        public Builder setFBC(String FBC) { this.FBC = FBC; return this; }

        public Builder setFM(String FM) { this.FM = FM; return this; }

        public Builder setPAS(String PAS) { this.PAS = PAS; return this; }

        public Builder setCOM(String COM) { this.COM = COM; return this; }

        public Builder setComments(String comments) { this.comments = comments; return this; }

        public Builder setMaleMVPs(List<String> maleMVPs) { this.maleMVPs = maleMVPs; return this; }

        public Builder setFemaleMVPs(List<String> femaleMVPs) { this.femaleMVPs = femaleMVPs; return this; }

        public Builder setDocument(Document doc) { this.doc = doc; return this; }

        public ReportFormState build(){ return new ReportFormState(this); }
    }
}
