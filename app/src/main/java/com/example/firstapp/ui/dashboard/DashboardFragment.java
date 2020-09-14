package com.example.firstapp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.DisplayUserActivity;
import com.example.firstapp.R;
import com.example.firstapp.model.Game;
import com.example.firstapp.model.User;
import com.example.firstapp.ui.missingresultgames.MissingResultAdapter;
import com.example.firstapp.ui.upcominggames.UpcomingAdapter;


public class DashboardFragment extends Fragment {

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
        upcomingrecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        upcomingrecyclerView.setLayoutManager(layoutManager);

        mAdapter = new UpcomingAdapter(limitupcomingItems(user.getUpcomingGames()));
        upcomingrecyclerView.setAdapter(mAdapter);

        resultsrecyclerView = (RecyclerView) v.findViewById(R.id.missing_result_recycler_view);
        resultsrecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        resultsrecyclerView.setLayoutManager(layoutManager);

        wAdapter = new MissingResultAdapter(limitupcomingItems(user.getMissingResultGames()));
        resultsrecyclerView.setAdapter(wAdapter);

        return v;
    }

    private Game[] limitupcomingItems(Game[] array){
        Game[] output = new Game[2];
        for(int i = 0; i < array.length; i++){
            output[i] = array[i];
            if(i == 1){ break; }
        }
        return output;
    }
}