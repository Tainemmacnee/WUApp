package com.example.wuapp.datamanagers;

import com.example.wuapp.parsers.WDSParser;
import com.example.wuapp.exceptions.InvalidLinkException;
import com.example.wuapp.model.ReportFormState;
import com.example.wuapp.model.UserLoginToken;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class ReportFormManager extends DataManager {

    public static final String REQUEST_REPORT_FORM = "request_form";

    private static ReportFormManager reportFormManager;
    private ReportFormState reportForm;

    private ReportFormManager(UserLoginToken loginToken){
        this.loginToken = loginToken;
    }

    public static void initialise(UserLoginToken loginToken){
        if(reportFormManager == null) {
            reportFormManager = new ReportFormManager(loginToken);
        }
    }

    public static ReportFormManager getInstance() { return reportFormManager; }

    @Override
    public void requestData(DataReceiver.Request request) {
        this.request = request;
        downloadReportForm(request.link);
    }

    @Override
    public void reload() { exception = null; }

    private void prepareResponse(){
        ArrayList data = new ArrayList();
        data.add(reportForm);
        submitResponse(new DataReceiver.Response(data, exception, request));
    }

    public void downloadReportForm(String link){
        this.downloading = true;
        CompletableFuture.supplyAsync(() -> {
            try {
                return downloadWebPage(link);
            } catch (IOException | InvalidLinkException e) {
                throw new CompletionException(e);
            }
        }).thenApply(r -> {
            try {
                return WDSParser.parseReportForm(r);
            } catch (ParseException e) {
                throw new CompletionException(e);
            }
        }).thenAccept(r -> {
            reportForm = r;
            this.downloading = false;
            prepareResponse();
        }).whenComplete((msg, ex) -> {exception = ex; downloading = false; prepareResponse();});
    }
}
