package com.example.Wuapp.ui.dashboard;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Wuapp.DisplayUserActivity;
import com.example.Wuapp.R;
import com.example.Wuapp.model.Game;
import com.example.Wuapp.model.User;
import com.example.Wuapp.ui.RefreshableFragment;
import com.example.Wuapp.ui.games.GameAdapter;


public class DashboardFragment extends Fragment implements RefreshableFragment {

    private RecyclerView upcomingrecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private RecyclerView resultsrecyclerView;
    private RecyclerView.Adapter wAdapter;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        DisplayUserActivity activity = (DisplayUserActivity)getActivity();
        user = activity.getUser();

        upcomingrecyclerView = (RecyclerView) v.findViewById(R.id.upcoming_recycler_view);
        resultsrecyclerView = (RecyclerView) v.findViewById(R.id.missing_result_recycler_view);

        //setup dashboard display for upcoming games
        loadUpcomingDisplay(v);

        //setup dashboard display for missing result games
        loadMissingResultDisplay(v);

        return v;
    }

    private void loadUpcomingDisplay(View v){
        if(user.getUpcomingGamesAsArray().length == 0){
            TextView textView = v.findViewById(R.id.empty_events_text); //display text showing no games
            textView.setVisibility(View.VISIBLE);
        } else {
            TextView textView = v.findViewById(R.id.empty_events_text);
            textView.setVisibility(View.GONE);
            upcomingrecyclerView.setHasFixedSize(true);

            layoutManager = new LinearLayoutManager(getActivity());
            upcomingrecyclerView.setLayoutManager(layoutManager);

            mAdapter = new GameAdapter(limitAdapterItems(user.getUpcomingGamesAsArray()));
            upcomingrecyclerView.setAdapter(mAdapter);
        }
    }

    private void loadMissingResultDisplay(View v){
        if(user.getMissingResultGamesAsArray().length == 0){
            TextView textView = v.findViewById(R.id.empty_games_text2);  //display text showing no games
            textView.setVisibility(View.VISIBLE);
        } else {
            TextView textView = v.findViewById(R.id.empty_games_text2);
            textView.setVisibility(View.GONE);
            resultsrecyclerView.setHasFixedSize(true);

            layoutManager = new LinearLayoutManager(getActivity());
            resultsrecyclerView.setLayoutManager(layoutManager);

            wAdapter = new GameAdapter(limitAdapterItems(user.getMissingResultGamesAsArray()));
            resultsrecyclerView.setAdapter(wAdapter);
        }
    }

    private Game[] limitAdapterItems(Game[] array){
        if(array.length < 2) { return array; }
        Game[] output = new Game[2];
        for(int i = 0; i < array.length; i++){
            output[i] = array[i];
            if(i == 1){ break; }
        }
        return output;
    }

    public void refresh() {
        user.loadData();

        //animate refresh button
        ActionMenuItemView image = getActivity().findViewById(R.id.action_refresh);
        Animation rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        rotateAnimation.setFillAfter(true);
        image.startAnimation(rotateAnimation);

        //Clear text and recyclers to show they are being reloaded
        getView().findViewById(R.id.empty_events_text).setVisibility(View.GONE); //display text showing no games
        getView().findViewById(R.id.empty_games_text2).setVisibility(View.GONE); //display text showing no games
        upcomingrecyclerView.setAdapter(new GameAdapter(new Game[0])); //clear current displayed events
        resultsrecyclerView.setAdapter(new GameAdapter(new Game[0]));

        //wait for data to load and display once done
        Handler handler = new Handler();
        int delay = 100; //milliseconds

        handler.postDelayed(new Runnable(){ //show new events after they are loaded
            public void run(){
                if(user.gamesDone()) {
                    loadUpcomingDisplay(getView());
                    loadMissingResultDisplay(getView());
                    image.clearAnimation(); //finish animation
                } else {
                    handler.postDelayed(this, delay);
                }
            }}, delay);
    }
}