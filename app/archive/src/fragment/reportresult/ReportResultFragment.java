//package com.example.wuapp.ui.fragment.reportresult;
//
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.example.wuapp.R;
//import com.example.wuapp.ui.activity.ReportResultActivity;
//import com.example.wuapp.databinding.FragmentReportResultBinding;
//import com.example.wuapp.databinding.ReportResultMvpBoxBinding;
//import com.example.wuapp.model.ReportFormState;
//import com.example.wuapp.model.Team;
//import com.squareup.picasso.Picasso;
//
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//
//public class ReportResultFragment extends Fragment {
//
//    private FragmentReportResultBinding binding;
//    private List<ReportResultMvpBoxBinding> maleMVPBindings = new ArrayList<>();
//    private List<ReportResultMvpBoxBinding> femaleMVPBindings = new ArrayList<>();
//
//    public List<String> getScoreValues(){
//        ArrayList<String> list = new ArrayList<>(Arrays.asList("Unreported", "Win", "Loss", "Tie(Unplayed)"));
//        for(int i = 0; i < 100; i++){
//            list.add(""+i);
//        }
//        return list;
//    }
//
//    public List<String> getSpiritValues(){
//        return new ArrayList<>(Arrays.asList("", "0 - Poor", "1 - Not so good", "2 - Good", "3 - Very good", "4 - Excellent"));
//    }
//
//    public List<String> getMaleMvpValues(Team team){
//        ArrayList list =  new ArrayList<>();
//        list.add("");
//        list.addAll(team.getMaleMatchups());
//        return list;
//    }
//
//    public List<String> getFemaleMvpValues(Team team){
//        ArrayList list =  new ArrayList<>();
//        list.add("");
//        list.addAll(team.getFemaleMatchups());
//        return list;
//    }
//
//    public void report(View view){
//        ReportResultActivity activity = (ReportResultActivity) getActivity();
//
//        int homeScore = binding.reportResultHomeScore.getSelectedItemPosition();
//        int awayScore = binding.reportResultAwayScore.getSelectedItemPosition();
//        int spiritRules = binding.spinnerRules.getSelectedItemPosition();
//        int spiritFouls = binding.spinnerFouls.getSelectedItemPosition();
//        int spiritFair = binding.spinnerFair.getSelectedItemPosition();
//        int spiritPos = binding.spinnerPositiveAttitude.getSelectedItemPosition();
//        int spiritCom = binding.spinnerCommunication.getSelectedItemPosition();
//        String comments = binding.reportResultComments.getText().toString();
//
//        //get mvps
//        List<String> femaleMVPs = new ArrayList<>();
//        for(ReportResultMvpBoxBinding binding : femaleMVPBindings){
//            femaleMVPs.add((String) binding.mvpSpinner.getSelectedItem());
//        }
//
//        List<String> maleMVPs = new ArrayList<>();
//        for(ReportResultMvpBoxBinding binding : maleMVPBindings){
//            maleMVPs.add((String) binding.mvpSpinner.getSelectedItem());
//        }
//
//        ReportFormState state = new ReportFormState.Builder()
//                .setHomeTeamScore(homeScore)
//                .setAwayTeamScore(awayScore)
//                .setRKU(spiritRules)
//                .setFBC(spiritFouls)
//                .setFM(spiritFair)
//                .setPAS(spiritPos)
//                .setCOM(spiritCom)
//                .setComments(comments)
//                .setMaleMVPs(maleMVPs)
//                .setFemaleMVPs(femaleMVPs)
//                .setDocument(activity.getReportFormState().doc)
//                .build();
//
//        activity.submit(state);
//    }
//
//    private void setupSpinner(Spinner spinner, ArrayAdapter<String> spinnerAdapter, int selection){
//        spinner.setAdapter(spinnerAdapter);
//        spinner.setSelection(selection);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        binding = FragmentReportResultBinding.inflate(inflater, container, false);
//        View view = binding.getRoot();
//
//        ReportResultActivity activity = (ReportResultActivity) getActivity();
//        ReportFormState state = activity.getReportFormState();
//
//        binding.reportResultHomeName.setText(activity.getHomeTeam().getName());
//        binding.reportResultAwayName.setText(activity.getAwayTeam().getName());
//        Picasso.get().load(activity.getHomeTeam().getImageUrl()).into(binding.homeImage);
//        Picasso.get().load(activity.getAwayTeam().getImageUrl()).into(binding.awayImage);
//
//        //Setup Adapters
//        ArrayAdapter<String> scoreAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.spinner_item, getScoreValues());
//        ArrayAdapter<String> spiritAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.spinner_item, getSpiritValues());
//
//        //Setup Spinners
//        setupSpinner(binding.reportResultHomeScore, scoreAdapter, state.homeTeamScore);
//        setupSpinner(binding.reportResultAwayScore, scoreAdapter, state.awayTeamScore);
//        setupSpinner(binding.spinnerRules, spiritAdapter, state.RKU);
//        setupSpinner(binding.spinnerFouls, spiritAdapter, state.FBC);
//        setupSpinner(binding.spinnerFair, spiritAdapter, state.FM);
//        setupSpinner(binding.spinnerPositiveAttitude, spiritAdapter, state.PAS);
//        setupSpinner(binding.spinnerCommunication, spiritAdapter, state.COM);
//
//        //setup MVPs
//        if(state.femaleMVPs.size() == 0 && state.maleMVPs.size() == 0){
//            binding.mvpboxTitle.setVisibility(View.GONE);
//            binding.femaleMvpbox.setVisibility(View.GONE);
//            binding.maleMvpbox.setVisibility(View.GONE);
//        } else {
//            binding.mvpboxTitle.setVisibility(View.VISIBLE);
//            binding.femaleMvpbox.setVisibility(View.VISIBLE);
//            binding.maleMvpbox.setVisibility(View.VISIBLE);
//
//            int maleMVPCount = 1, femaleMVPCount = 1;
//
//            for(String name : state.femaleMVPs){
//                ArrayAdapter<String> femaleMVPAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.spinner_item, this.getFemaleMvpValues(activity.getOtherTeam()));
//
//                ReportResultMvpBoxBinding mvpBoxBinding = ReportResultMvpBoxBinding.inflate(inflater);
//                mvpBoxBinding.mvpTitle.setText("Female MVP #"+femaleMVPCount++);
//                mvpBoxBinding.mvpSpinner.setAdapter(femaleMVPAdapter);
//                mvpBoxBinding.mvpSpinner.setSelection(femaleMVPAdapter.getPosition(name));
//
//                binding.femaleMvpbox.addView(mvpBoxBinding.getRoot());
//                femaleMVPBindings.add(mvpBoxBinding);
//            }
//
//            for(String name : state.maleMVPs){
//                ArrayAdapter<String> maleMVPAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.spinner_item, this.getMaleMvpValues(activity.getOtherTeam()));
//
//                ReportResultMvpBoxBinding mvpBoxBinding = ReportResultMvpBoxBinding.inflate(inflater);
//                mvpBoxBinding.mvpTitle.setText("Male MVP #"+maleMVPCount++);
//                mvpBoxBinding.mvpSpinner.setAdapter(maleMVPAdapter);
//                mvpBoxBinding.mvpSpinner.setSelection(maleMVPAdapter.getPosition(name));
//
//                binding.maleMvpbox.addView(mvpBoxBinding.getRoot());
//                maleMVPBindings.add(mvpBoxBinding);
//            }
//        }
//
//        //setup comments
//        binding.reportResultComments.setText(state.comments);
//
//        //Setup report button
//        binding.reportResults2.setOnClickListener(this::report);
//
//        return view;
//    }
//}