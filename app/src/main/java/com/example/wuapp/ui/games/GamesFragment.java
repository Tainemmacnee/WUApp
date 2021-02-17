package com.example.wuapp.ui.games;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wuapp.DisplayUserActivity;
import com.example.wuapp.MainActivity;
import com.example.wuapp.R;
import com.example.wuapp.data.DataManager;
import com.example.wuapp.data.DataReceiver;
import com.example.wuapp.model.Event;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.User;
import com.example.wuapp.ui.RefreshableFragment;
import com.example.wuapp.ui.events.EventsAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class GamesFragment extends Fragment implements DataReceiver {

    private RecyclerView recyclerView;
    private GameAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_games, container, false);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        setHasOptionsMenu(true);

        MainActivity activity = (MainActivity) getActivity();
        makeRequest(activity.getDataManager(), this, DataManager.REQUEST_SCHEDULED_GAMES);

        return v;
    }

    private void loadRecycleView(ArrayList<Game> data){
        Collections.sort(data, new Game.SortByDate());
        recyclerView.setAdapter(new GameAdapter2(data));
    }

    @Override
    public <T> void receiveData(ArrayList<T> results) {
        if(results != null && results.size() > 0){
            if(results.get(0) instanceof Game){
                loadRecycleView((ArrayList<Game>) results);
            }
        }
    }

}