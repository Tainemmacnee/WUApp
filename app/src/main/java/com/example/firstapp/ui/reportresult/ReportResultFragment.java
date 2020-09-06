package com.example.firstapp.ui.reportresult;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.firstapp.R;
import com.example.firstapp.ReportResultActivity;
import com.example.firstapp.model.Event;
import com.example.firstapp.model.ReportFormState;
import com.example.firstapp.model.Team;
import com.squareup.picasso.Picasso;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class ReportResultFragment extends Fragment {

    private int mspCount = 1;

    public ReportResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public List<String> getScoreValues(){
        ArrayList<String> list = new ArrayList<>(Arrays.asList("Unreported", "Win", "Loss", "Tie(Unplayed)"));
        for(int i = 0; i < 100; i++){
            list.add(""+i);
        }
        return list;
    }

    public List<String> getSpiritValues(){
        return new ArrayList<>(Arrays.asList("", "0 - Poor", "1 - Not so good", "2 - Good", "3 - Very good", "4 - Excellent"));
    }

    public List<String> getMaleMvpValues(Team team){
        ArrayList list =  new ArrayList<>();
        list.add("");
        list.addAll(team.getMaleMatchups());
        System.out.println("MALE ADAPTER: "+list);
        return list;
    }

    public List<String> getFemaleMvpValues(Team team){
        ArrayList list =  new ArrayList<>();
        list.add("");
        list.addAll(team.getFemaleMatchups());
        System.out.println("FEMALE ADAPTER: "+list);
        return list;
    }

    public void report(View view){
        ReportResultActivity activity = (ReportResultActivity) getActivity();

        Spinner homeScoreSpinner = (Spinner) activity.findViewById(R.id.report_result_home_score);
        Spinner awayScoreSpinner = (Spinner) activity.findViewById(R.id.report_result_away_score);
        Spinner spiritRulesSpinner = (Spinner) activity.findViewById(R.id.spinner_rules);
        Spinner spiritFoulsSpinner = (Spinner) activity.findViewById(R.id.spinner_fouls);
        Spinner spiritFairSpinner = (Spinner) activity.findViewById(R.id.spinner_fair);
        Spinner spiritPosSpinner = (Spinner) activity.findViewById(R.id.spinner_positive_attitude);
        Spinner spiritComSpinner = (Spinner) activity.findViewById(R.id.spinner_communication);
        EditText comments = activity.findViewById(R.id.report_result_comments);

        String homeScore = (String) homeScoreSpinner.getSelectedItem();
        String awayScore = (String) awayScoreSpinner.getSelectedItem();
        String spiritRules = (String) spiritRulesSpinner.getSelectedItem();
        String spiritFouls = (String) spiritFoulsSpinner.getSelectedItem();
        String spiritFair = (String) spiritFairSpinner.getSelectedItem();
        String spiritPos = (String) spiritPosSpinner.getSelectedItem();
        String spiritCom = (String) spiritComSpinner.getSelectedItem();

        //get mvps
        List<String> mvpList = new ArrayList<>();
        LinearLayout mvpBox = activity.findViewById(R.id.mvpBox);
        for(int i = 0; i < mvpBox.getChildCount(); i++){
            LinearLayout mvpLayout = (LinearLayout) mvpBox.getChildAt(i);
            Spinner mvpSpinner = (Spinner) mvpLayout.getChildAt(1);
            mvpList.add((String) mvpSpinner.getSelectedItem());
        }

        activity.submit(homeScore, awayScore, spiritRules, spiritFouls, spiritFair, spiritPos, spiritCom, comments.getText().toString(), mvpList);

    }

    private void setupSpinner(Spinner spinner, ArrayAdapter<String> spinnerAdapter, int selection){
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(selection);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ReportResultActivity activity = (ReportResultActivity) getActivity();
        ReportFormState state = activity.getReportFormState();
        View view = inflater.inflate(R.layout.fragment_report_result, container, false);

        //Setup Adapters
        ArrayAdapter<String> scoreAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.spinner_item, getScoreValues());
        ArrayAdapter<String> spiritAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.spinner_item, getSpiritValues());

        //Setup Spinners
        setupSpinner(view.findViewById(R.id.report_result_home_score), scoreAdapter, scoreAdapter.getPosition(state.homeTeamScore));
        setupSpinner(view.findViewById(R.id.report_result_away_score), scoreAdapter, scoreAdapter.getPosition(state.awayTeamScore));
        setupSpinner(view.findViewById(R.id.spinner_rules), spiritAdapter, spiritAdapter.getPosition(state.RKU));
        setupSpinner(view.findViewById(R.id.spinner_fouls), spiritAdapter, spiritAdapter.getPosition(state.FBC));
        setupSpinner(view.findViewById(R.id.spinner_fair), spiritAdapter, spiritAdapter.getPosition(state.FM));
        setupSpinner(view.findViewById(R.id.spinner_positive_attitude), spiritAdapter, spiritAdapter.getPosition(state.PAS));
        setupSpinner(view.findViewById(R.id.spinner_communication), spiritAdapter, spiritAdapter.getPosition(state.COM));

        //Load team details
        TextView homeTeamName = view.findViewById(R.id.report_result_home_name);
        TextView awayTeamName = view.findViewById(R.id.report_result_away_name);
        ImageView homeTeamImage = view.findViewById(R.id.report_result_home_image);
        ImageView awayTeamImage = view.findViewById(R.id.report_result_away_image);

        homeTeamName.setText(activity.getHomeTeam().getName());
        awayTeamName.setText(activity.getAwayTeam().getName());
        Picasso.get().load(activity.getHomeTeam().getImageUrl()).into(homeTeamImage);
        Picasso.get().load(activity.getAwayTeam().getImageUrl()).into(awayTeamImage);

        //Setup mvps
        LinearLayout mvpBox = view.findViewById(R.id.mvpBox);
        int maleMVPCount = 1, femaleMVPCount = 1;

        for(String name : state.femaleMVPs){
            ArrayAdapter<String> femaleMVPAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.spinner_item, this.getFemaleMvpValues(activity.getOtherTeam()));
            LinearLayout mvp = buildMVPBox(view, name, "Female MVP #"+femaleMVPCount++, femaleMVPAdapter);
            mvpBox.addView(mvp);
        }

        for(String name : state.maleMVPs){
            ArrayAdapter<String> maleMVPAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.spinner_item, this.getMaleMvpValues(activity.getOtherTeam()));
            LinearLayout mvp = buildMVPBox(view, name, "Male MVP #"+maleMVPCount++, maleMVPAdapter);
            mvpBox.addView(mvp);
        }

        //load comments
        EditText comments = view.findViewById(R.id.report_result_comments);
        comments.setText(state.comments);

        Button reportButton = view.findViewById(R.id.report_results2);
        reportButton.setOnClickListener(this::report);

        return view;
    }

    private LinearLayout buildMVPBox(View view, String name, String title, ArrayAdapter<String> adapter){
        //setup container for mvp
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams((int) (200*getContext().getResources().getDisplayMetrics().density), LinearLayout.LayoutParams.WRAP_CONTENT));

        //setup title for mvp
        TextView titleView = new TextView(getContext());
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleView.setText(title);
        titleView.setTextColor(getResources().getColor(R.color.colorPrimary));

        //setup spinner for mvp
        Spinner spinner = new Spinner(getContext());
        spinner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (40*getContext().getResources().getDisplayMetrics().density)));
        spinner.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_background_light));
        spinner.setAdapter(adapter);

        //set spinner selection
        if(!name.equals("")) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).contains(name)) {
                    spinner.setSelection(i);
                }
            }
        }

        layout.addView(titleView, 0);
        layout.addView(spinner, 1);
        return layout;
    }
}