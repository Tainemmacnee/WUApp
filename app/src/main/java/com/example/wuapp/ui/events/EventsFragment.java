package com.example.wuapp.ui.events;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wuapp.MainActivity;
import com.example.wuapp.R;
import com.example.wuapp.data.DataManager;
import com.example.wuapp.data.DataReceiver;
import com.example.wuapp.databinding.FragmentEventsBinding;
import com.example.wuapp.model.Event;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.User;

import java.util.ArrayList;

public class EventsFragment extends Fragment implements DataReceiver {

    private RecyclerView.LayoutManager layoutManager;
    private MainActivity activity;
    private FragmentEventsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventsBinding.inflate(inflater);

        layoutManager = new LinearLayoutManager(getActivity());
        binding.eventRecyclerView.setLayoutManager(layoutManager);

        activity = (MainActivity) getActivity();
        makeRequest(activity.getDataManager(), this, DataManager.REQUEST_EVENTS);

        return binding.getRoot();
    }

    private void loadRecycleView(ArrayList<Event> data){
        binding.eventRecyclerView.setAdapter(new EventsAdapter(data, activity.getDataManager()));
    }

    @Override
    public <T> void receiveData(ArrayList<T> results) {
        if(results != null) {

            binding.loadingView.getRoot().setVisibility(View.GONE); //Hide loading animation

            if (results.size() > 0) {
                if (results.get(0) instanceof Event) { //Display games
                    loadRecycleView((ArrayList<Event>) results);
                }
            } else { //Display info text
                binding.noEventsText.setVisibility(View.VISIBLE);
            }
        }
    }
}