package com.example.wuapp.activities.main.games;

import android.view.View;

import com.example.wuapp.activities.main.DisplayFragment;
import com.example.wuapp.activities.main.MainActivity;
import com.example.wuapp.databinding.FragmentExceptionDisplayBinding;
import com.example.wuapp.datamanagers.DataReceiver;
import com.example.wuapp.datamanagers.GamesManager;

import java.util.List;

public class PlayedGamesFragment extends DisplayFragment {

    @Override
    public void loadData(List data) {
        binding.swipeRefresh.setRefreshing(false);
        binding.loadingView.getRoot().setVisibility(View.GONE);//Hide loading animation
        binding.recyclerView.setAdapter(new GameAdapter(data));
    }

    @Override
    protected void refresh() {
        GamesManager.getInstance().reload();
        GamesManager.getInstance().requestData(new DataReceiver.Request(this, GamesManager.REQUEST_RECENT_GAMES));
    }

    public void loadErrorMessage(String message){
        super.loadErrorMessage(message);
        FragmentExceptionDisplayBinding errorView = binding.errorView;
        errorView.reloadButton.setText("Reload Games");
        errorView.reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).reloadGames(null);
            }
        });
    }
}