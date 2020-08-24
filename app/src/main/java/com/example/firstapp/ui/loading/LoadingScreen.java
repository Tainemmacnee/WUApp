package com.example.firstapp.ui.loading;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.firstapp.R;

import org.w3c.dom.Text;

import java.util.concurrent.Future;


public class LoadingScreen extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    public LoadingScreen() {
        // Required empty public constructor
    }

    public static LoadingScreen newInstance(String param1) {
        LoadingScreen fragment = new LoadingScreen();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loading_screen, container, false);

        TextView message = view.findViewById(R.id.loading_text);
        message.setText(this.mParam1);

        return view;
    }
}