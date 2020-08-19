package com.example.firstapp.ui.events.teams;

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
import com.example.firstapp.ui.events.EventsAdapter;

public class EventTeamsFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String eventName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_teams, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.event_team_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        DisplayUserActivity activity = (DisplayUserActivity)getActivity();
        User user = activity.getUserData();
        mAdapter = new EventTeamsAdapter(user.getEvent(eventName).getTeams());
        recyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.eventName = bundle.getString("eventName");
        }
    }
}
