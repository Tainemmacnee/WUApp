package com.example.firstapp.ui.missingresultgames;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.DisplayUserActivity;
import com.example.firstapp.R;
import com.example.firstapp.model.User;

public class MissingResultGamesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_missing_result_games, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.missing_result_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        DisplayUserActivity activity = (DisplayUserActivity)getActivity();
        User user = activity.getUserData();
        mAdapter = new MissingResultAdapter(user.getMissingResultGames());
        recyclerView.setAdapter(mAdapter);

        return v;
    }
}