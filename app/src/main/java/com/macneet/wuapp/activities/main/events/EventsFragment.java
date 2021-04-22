package com.macneet.wuapp.activities.main.events;

import android.view.View;

import com.macneet.wuapp.activities.main.DisplayFragment;
import com.macneet.wuapp.datamanagers.EventsManager;

import java.util.List;

public class EventsFragment extends DisplayFragment {

    @Override
    public void loadData(List data) {
        binding.swipeRefresh.setRefreshing(false);
        binding.loadingView.getRoot().setVisibility(View.GONE);//Hide loading animation
        binding.recyclerView.setAdapter(new EventsAdapter(data));
    }

    @Override
    protected void refresh() {
        EventsManager.getInstance().requestData(new Request(this, EventsManager.REQUEST_EVENTS));
    }

    @Override
    public void loadNoDataMessage() {
        super.loadNoDataMessage();
        binding.infoMessage.setText("No Events Were Found");
    }
}