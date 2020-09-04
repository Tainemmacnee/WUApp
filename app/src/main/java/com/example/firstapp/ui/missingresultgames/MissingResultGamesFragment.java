package com.example.firstapp.ui.missingresultgames;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.DisplayUserActivity;
import com.example.firstapp.R;
import com.example.firstapp.model.Event;
import com.example.firstapp.model.Game;
import com.example.firstapp.model.User;
import com.example.firstapp.ui.RefreshableFragment;
import com.example.firstapp.ui.events.EventsAdapter;

public class MissingResultGamesFragment extends Fragment implements RefreshableFragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_missing_result_games, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.missing_result_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        DisplayUserActivity activity = (DisplayUserActivity)getActivity();
        user = activity.getUserData();
        mAdapter = new MissingResultAdapter(user.getMissingResultGames());
        recyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void refresh() {
        recyclerView.setAdapter(new MissingResultAdapter(new Game[0])); //clear current displayed events
        user.loadExtras();
        Handler handler = new Handler();
        int delay = 100; //milliseconds

        handler.postDelayed(new Runnable(){ //show new events after they are loaded
            public void run(){
                mAdapter = new MissingResultAdapter(user.getMissingResultGames());
                recyclerView.setAdapter(mAdapter);
            }}, delay);
    }
}