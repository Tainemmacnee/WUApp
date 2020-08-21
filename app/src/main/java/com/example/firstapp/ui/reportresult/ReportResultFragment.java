package com.example.firstapp.ui.reportresult;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.firstapp.R;

import java.util.ArrayList;
import java.util.Arrays;


public class ReportResultFragment extends Fragment {



    public ReportResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_result, container, false);
        ArrayList<String> list = new ArrayList<String>(Arrays.asList("Unreported", "Win", "Loss", "Tie(Unplayed)"));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), R.layout.spinner_item, list);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        Spinner spinner2 = (Spinner) view.findViewById(R.id.spinner2);
        spinner.setAdapter(adapter);
        spinner2.setAdapter(adapter);

        return view;
    }
}