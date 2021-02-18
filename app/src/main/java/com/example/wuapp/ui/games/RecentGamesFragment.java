package com.example.wuapp.ui.games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wuapp.MainActivity;
import com.example.wuapp.R;
import com.example.wuapp.data.DataManager;
import com.example.wuapp.data.DataReceiver;
import com.example.wuapp.model.Game;

import java.util.ArrayList;
import java.util.Collections;

public class RecentGamesFragment extends Fragment implements DataReceiver {

    private RecyclerView recyclerView;
    private GameAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_games, container, false);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        setHasOptionsMenu(true);

        MainActivity activity = (MainActivity) getActivity();
        makeRequest(activity.getDataManager(), this, DataManager.REQUEST_RECENT_GAMES);

        return v;
    }

    private void loadRecycleView(ArrayList<Game> data){
        Collections.sort(data, new Game.SortByLeastRecentDate());
        recyclerView.setAdapter(new GameAdapter2(data));
    }

    @Override
    public <T> void receiveData(ArrayList<T> results) {
        if(results != null && results.size() > 0){
            if(results.get(0) instanceof Game){
                loadRecycleView((ArrayList<Game>) results);
            }
        }
    }
}
