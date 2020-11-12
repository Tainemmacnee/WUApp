package com.example.wuapp.ui.dashboard;

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

import com.example.wuapp.DisplayUserActivity;
import com.example.wuapp.R;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.User;
import com.example.wuapp.ui.RefreshableFragment;
import com.example.wuapp.ui.games.GameAdapter;
import com.example.wuapp.ui.games.NoScrollLinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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
        if(user.getUpcomingGames().size() == 0){
            TextView textView = v.findViewById(R.id.empty_events_text); //display text showing no games
            textView.setVisibility(View.VISIBLE);
        } else {
            TextView textView = v.findViewById(R.id.empty_events_text);
            textView.setVisibility(View.GONE);
            upcomingrecyclerView.setHasFixedSize(true);
            upcomingrecyclerView.setLayoutManager(new NoScrollLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

            mAdapter = new GameAdapter(limitAdapterItems(user.getUpcomingGames()),  user.getEvents());
            upcomingrecyclerView.setAdapter(mAdapter);
        }
    }

    private void loadMissingResultDisplay(View v){
        if(user.getMissingResultGames().size() == 0){
            TextView textView = v.findViewById(R.id.empty_games_text2);  //display text showing no games
            textView.setVisibility(View.VISIBLE);
        } else {
            TextView textView = v.findViewById(R.id.empty_games_text2);
            textView.setVisibility(View.GONE);
            resultsrecyclerView.setHasFixedSize(true);
            resultsrecyclerView.setLayoutManager(new NoScrollLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

            wAdapter = new GameAdapter(limitAdapterItems(user.getMissingResultGames()),  user.getEvents());
            resultsrecyclerView.setAdapter(wAdapter);
        }
    }

    private List<Game> limitAdapterItems(List<Game> array){
        if(array.size() < 2) { return array; }
        return array.subList(0, 2);
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
        upcomingrecyclerView.setAdapter(new GameAdapter(Collections.emptyList(), Collections.emptyList())); //clear current displayed events
        resultsrecyclerView.setAdapter(new GameAdapter(Collections.emptyList(), Collections.emptyList()));

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