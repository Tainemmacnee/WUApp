package com.example.firstapp.ui.missingresultgames;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        DisplayUserActivity activity = (DisplayUserActivity)getActivity();
        user = activity.getUserData();

        loadDisplay(v);

        return v;
    }

    private void loadDisplay(View v){
        if(user.getMissingResultGames().length == 0){
            TextView textView = v.findViewById(R.id.empty_games_text); //display text showing no games
            textView.setVisibility(View.VISIBLE);
        } else {
            TextView textView = v.findViewById(R.id.empty_games_text);
            textView.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);

            layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);

            mAdapter = new MissingResultAdapter(user.getMissingResultGames());
            recyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void refresh() {
        recyclerView.setAdapter(new MissingResultAdapter(new Game[0])); //clear current displayed events
        TextView textView = getView().findViewById(R.id.empty_games_text);
        textView.setVisibility(View.GONE);
        user.loadExtras();
        Handler handler = new Handler();
        int delay = 100; //milliseconds

        handler.postDelayed(new Runnable(){ //show new events after they are loaded
            public void run(){
                loadDisplay(getView());
            }}, delay);
    }
}