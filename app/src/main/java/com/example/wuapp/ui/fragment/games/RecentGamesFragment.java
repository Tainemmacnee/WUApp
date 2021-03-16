package com.example.wuapp.ui.fragment.games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wuapp.ui.activity.MainActivity;
import com.example.wuapp.data.DataManager;
import com.example.wuapp.data.DataReceiver;
import com.example.wuapp.databinding.FragmentGamesBinding;
import com.example.wuapp.model.Game;

import java.util.ArrayList;
import java.util.Collections;

public class RecentGamesFragment extends Fragment implements DataReceiver {

    private RecyclerView.LayoutManager layoutManager;
    private FragmentGamesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGamesBinding.inflate(inflater);

        layoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(layoutManager);

        MainActivity activity = (MainActivity) getActivity();
        makeRequest(activity.getDataManager(), this, DataManager.REQUEST_RECENT_GAMES);

        return binding.getRoot();
    }

    private void loadRecycleView(ArrayList<Game> data){
        Collections.sort(data, new Game.SortByLeastRecentDate());
        binding.recyclerView.setAdapter(new GameAdapter(data));
    }

    @Override
    public <T> void receiveData(ArrayList<T> results) {
        if(results != null) {

            binding.loadingView.getRoot().setVisibility(View.GONE); //Hide loading animation

            if (results.size() > 0) {
                if (results.get(0) instanceof Game) { //Display games
                    loadRecycleView((ArrayList<Game>) results);
                }
            } else { //Display info text
                binding.noGamesText.setVisibility(View.VISIBLE);
            }
        }
    }
}
