package com.example.firstapp.model;

import android.widget.LinearLayout;

import org.jsoup.nodes.Document;

import java.util.List;

public class ReportFormState{

    public final String homeTeamScore, awayTeamScore, RKU, FBC, FM, PAS, COM, comments;
    public final List<String> maleMVPs, femaleMVPs;
    public final Document doc;

    public ReportFormState(Document doc, String homeTeamScore, String awayTeamScore, String RKU, String FBC, String FM, String PAS, String COM, String comments, List<String> maleMVPs, List<String> femaleMVPs){

        this.RKU = (RKU.length() == 0 ? "2 - Good" : RKU);
        this.FBC = (RKU.length() == 0 ? "2 - Good" : FBC);
        this.FM = (RKU.length() == 0 ? "2 - Good" : FM);
        this.PAS = (RKU.length() == 0 ? "2 - Good" : PAS);
        this.COM = (RKU.length() == 0 ? "2 - Good" : COM);

        this.homeTeamScore = homeTeamScore;
        this.awayTeamScore = awayTeamScore;
        this.comments = comments;
        this.doc = doc;
        this.femaleMVPs = femaleMVPs;
        this.maleMVPs = maleMVPs;
    }
}
