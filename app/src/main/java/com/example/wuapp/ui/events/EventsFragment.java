package com.example.wuapp.ui.events;

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
import com.example.wuapp.model.Event;
import com.example.wuapp.model.User;

import java.util.ArrayList;

public class EventsFragment extends Fragment implements DataReceiver {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_events, container, false);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView = v.findViewById(R.id.event_recycler_view);
        recyclerView.setLayoutManager(layoutManager);

        MainActivity activity = (MainActivity) getActivity();

        makeRequest(activity.getDataManager(), this, DataManager.REQUEST_EVENTS);

        return v;
    }

    private void loadRecycleView(ArrayList<Event> data){
        recyclerView.setAdapter(new EventsAdapter(data));
    }

    @Override
    public <T> void receiveData(ArrayList<T> results) {
        if(results != null && results.size() > 0){
            if(results.get(0) instanceof Event){
                System.out.println("Setting Events");
                loadRecycleView((ArrayList<Event>) results);
            }
        }
    }
}