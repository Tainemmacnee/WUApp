package com.example.wuapp.ui.loading;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wuapp.R;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class LoadingScreen extends Fragment {

    private String loadingMessage;
    private Future load;
    private loadableActivity activity;

    public interface loadableActivity {

        public void processResult(Object Result, boolean finished);

    }

    public LoadingScreen(){}

    public LoadingScreen(String loadingMessage, Future load, loadableActivity activity){
        this.loadingMessage = loadingMessage;
        this.load = load;
        this.activity = activity;

//        AppCompatActivity tempActivity = (AppCompatActivity) activity;
//        FragmentTransaction transaction = tempActivity.getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id., this);
//        transaction.commit();

        start();
    }

    private void start(){
        Handler handler = new Handler();
        int delay = 500; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(load.isDone()){
                    try{
                        activity.processResult(load.get(), true);
                    } catch (Exception e) {
                        activity.processResult(null, true);
                    }
                } else {
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loading_screen, container, false);
        TextView message = view.findViewById(R.id.loading_text);
        message.setText(loadingMessage);

        return view;
    }
}