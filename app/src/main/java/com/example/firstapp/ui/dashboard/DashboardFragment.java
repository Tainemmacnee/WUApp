package com.example.firstapp.ui.dashboard;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.DisplayUserActivity;
import com.example.firstapp.R;
import com.example.firstapp.model.Game;
import com.example.firstapp.model.User;
import com.example.firstapp.ui.RefreshableFragment;
import com.example.firstapp.ui.missingresultgames.MissingResultAdapter;
import com.example.firstapp.ui.upcominggames.UpcomingAdapter;


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
        user = activity.getUserData();

        upcomingrecyclerView = (RecyclerView) v.findViewById(R.id.upcoming_recycler_view);
        resultsrecyclerView = (RecyclerView) v.findViewById(R.id.missing_result_recycler_view);

        //setup dashboard display for upcoming games
        loadUpcomingDisplay(v);

        //setup dashboard display for missing result games
        loadMissingResultDisplay(v);

        return v;
    }

    private void loadUpcomingDisplay(View v){
        if(user.getUpcomingGames().length == 0){
            TextView textView = v.findViewById(R.id.empty_games_text); //display text showing no games
            textView.setVisibility(View.VISIBLE);
        } else {
            TextView textView = v.findViewById(R.id.empty_games_text);
            textView.setVisibility(View.GONE);
            upcomingrecyclerView.setHasFixedSize(true);

            layoutManager = new LinearLayoutManager(getActivity());
            upcomingrecyclerView.setLayoutManager(layoutManager);

            mAdapter = new UpcomingAdapter(limitupcomingItems(user.getUpcomingGames()));
            upcomingrecyclerView.setAdapter(mAdapter);
        }
    }

    private void loadMissingResultDisplay(View v){
        if(user.getMissingResultGames().length == 0){
            TextView textView = v.findViewById(R.id.empty_games_text2);  //display text showing no games
            textView.setVisibility(View.VISIBLE);
        } else {
            TextView textView = v.findViewById(R.id.empty_games_text2);
            textView.setVisibility(View.GONE);
            resultsrecyclerView.setHasFixedSize(true);

            layoutManager = new LinearLayoutManager(getActivity());
            resultsrecyclerView.setLayoutManager(layoutManager);

            wAdapter = new MissingResultAdapter(limitupcomingItems(user.getMissingResultGames()));
            resultsrecyclerView.setAdapter(wAdapter);
        }
    }

    private Game[] limitupcomingItems(Game[] array){
        if(array.length < 2) { return array; }
        Game[] output = new Game[2];
        for(int i = 0; i < array.length; i++){
            output[i] = array[i];
            if(i == 1){ break; }
        }
        return output;
    }

    public void refresh() {
        TextView textView1 = getView().findViewById(R.id.empty_games_text); //display text showing no games
        textView1.setVisibility(View.GONE);
        TextView textView2 = getView().findViewById(R.id.empty_games_text2); //display text showing no games
        textView2.setVisibility(View.GONE);
        upcomingrecyclerView.setAdapter(new UpcomingAdapter(new Game[0])); //clear current displayed events
        resultsrecyclerView.setAdapter(new MissingResultAdapter(new Game[0]));
        user.loadExtras();
        Handler handler = new Handler();
        int delay = 100; //milliseconds

        handler.postDelayed(new Runnable(){ //show new events after they are loaded
            public void run(){
                loadUpcomingDisplay(getView());
                loadMissingResultDisplay(getView());
            }}, delay);
    }
}