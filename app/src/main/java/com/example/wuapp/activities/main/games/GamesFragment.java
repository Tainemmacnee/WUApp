package com.example.wuapp.activities.main.games;

import android.view.View;

import com.example.wuapp.activities.main.DisplayFragment;
import com.example.wuapp.activities.main.MainActivity;
import com.example.wuapp.databinding.FragmentExceptionDisplayBinding;

import java.util.List;

public class GamesFragment extends DisplayFragment {

    @Override
    public void loadData(List data) {
        binding.loadingView.getRoot().setVisibility(View.GONE);//Hide loading animation
        binding.recyclerView.setAdapter(new GameAdapter(data));
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