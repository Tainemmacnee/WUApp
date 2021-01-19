package com.example.wuapp.ui.events.teams;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wuapp.DisplayEventTeamsActivity;
import com.example.wuapp.R;
import com.example.wuapp.model.Team;

public class EventTeamsFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String eventName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_teams, container, false);

        DisplayEventTeamsActivity activity = (DisplayEventTeamsActivity)getActivity();
        Team[] eventTeams = activity.getEventTeams();

        recyclerView = v.findViewById(R.id.event_team_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new EventTeamsAdapter(eventTeams);
        recyclerView.setAdapter(mAdapter);

        activity.setmAdapter((EventTeamsAdapter) mAdapter);

        return v;
    }

}