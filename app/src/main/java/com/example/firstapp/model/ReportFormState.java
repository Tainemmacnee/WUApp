package com.example.firstapp.model;

import android.widget.LinearLayout;

import org.jsoup.nodes.Document;

import java.util.List;

public class ReportFormState{

    public String homeTeamScore, awayTeamScore, RKU, FBC, FM, PAS, COM, comments;
    public List<String> maleMVPs, femaleMVPs;
    public Document doc;

    public ReportFormState(Document doc, String homeTeamScore, String awayTeamScore, String RKU, String FBC, String FM, String PAS, String COM, String comments, List<String> maleMVPs, List<String> femaleMVPs){

        this.RKU = RKU;
        this.FBC = FBC;
        this.FM = FM;
        this.PAS = PAS;
        this.COM = COM;

        this.homeTeamScore = homeTeamScore;
        this.awayTeamScore = awayTeamScore;
        this.comments = comments;
        this.doc = doc;
        this.femaleMVPs = femaleMVPs;
        this.maleMVPs = maleMVPs;
    }
}
