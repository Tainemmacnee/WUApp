package com.example.wuapp.activities.main.events;

import android.view.View;

import com.example.wuapp.activities.main.MainActivity;
import com.example.wuapp.activities.main.DisplayFragment;

import java.util.List;

public class EventsFragment extends DisplayFragment {

    @Override
    public void loadData(List data) {
        MainActivity activity = (MainActivity) getActivity();
        binding.loadingView.getRoot().setVisibility(View.GONE);//Hide loading animation
        binding.recyclerView.setAdapter(new EventsAdapter(data));
    }

    @Override
    public void loadNoDataMessage() {
        super.loadNoDataMessage();
        binding.infoMessage.setText("No Events Were Found");
    }
}