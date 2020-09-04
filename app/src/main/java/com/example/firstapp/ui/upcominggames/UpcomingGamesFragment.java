package com.example.firstapp.ui.upcominggames;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.DisplayUserActivity;
import com.example.firstapp.R;
import com.example.firstapp.model.Event;
import com.example.firstapp.model.Game;
import com.example.firstapp.model.User;
import com.example.firstapp.ui.RefreshableFragment;
import com.example.firstapp.ui.events.EventsAdapter;

public class UpcomingGamesFragment extends Fragment implements RefreshableFragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_upcoming_games, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.upcoming_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        DisplayUserActivity activity = (DisplayUserActivity)getActivity();
        user = activity.getUserData();
        mAdapter = new UpcomingAdapter(user.getUpcomingGames());
        recyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void refresh() {
        recyclerView.setAdapter(new UpcomingAdapter(new Game[0])); //clear current displayed events
        user.loadExtras();
        Handler handler = new Handler();
        int delay = 100; //milliseconds

        handler.postDelayed(new Runnable(){ //show new events after they are loaded
            public void run(){
                mAdapter = new UpcomingAdapter(user.getUpcomingGames());
                recyclerView.setAdapter(mAdapter);
            }}, delay);
    }
}