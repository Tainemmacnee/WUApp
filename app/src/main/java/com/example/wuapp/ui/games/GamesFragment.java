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
import com.example.wuapp.R;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.User;
import com.example.wuapp.ui.RefreshableFragment;

import java.util.ArrayList;

public class GamesFragment extends Fragment implements RefreshableFragment, FilterDialog.FilterDialogListner{

    private RecyclerView recyclerView;
    private GameAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private User user;
    private FilterDialog filterDialog = new FilterDialog(this);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_games, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        setHasOptionsMenu(true);

        DisplayUserActivity activity = (DisplayUserActivity)getActivity();
        user = activity.getUser();

        loadDisplay(v);

        return v;
    }

    public void showFilterDialog(){
        filterDialog.show(getParentFragmentManager(), "FilterDialog");
    }

    @Override
    public void onDialogPositiveClick(String filter) {
        mAdapter.getFilter().filter(filter);
        if(mAdapter.getItemCount() == 0){
            TextView textView = getView().findViewById(R.id.empty_events_text); //display text showing no games
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void loadDisplay(View v){
        TextView textView = v.findViewById(R.id.empty_events_text);
        textView.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new GameAdapter(user.getGames());
        recyclerView.setAdapter(mAdapter);

        onDialogPositiveClick(filterDialog.filter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.display_games_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
       if(item.getItemId() == R.id.action_filter) {
            showFilterDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void refresh() {
        user.loadData();

        //animate refresh button
        ActionMenuItemView image = getActivity().findViewById(R.id.action_refresh);
        Animation rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        rotateAnimation.setFillAfter(true);
        image.startAnimation(rotateAnimation);

        //Clear text and recycler to show they are being reloaded
        recyclerView.setAdapter(new GameAdapter(new ArrayList<>())); //clear current displayed events
        getView().findViewById(R.id.empty_events_text).setVisibility(View.GONE);

        //wait for data to load and display once done
        Handler handler = new Handler();
        int delay = 100; //milliseconds

        handler.postDelayed(new Runnable(){ //show new events after they are loaded
            public void run(){
                if(user.gamesDone()){
                    loadDisplay(getView());
                    image.clearAnimation(); //finish animation
                } else {
                    handler.postDelayed(this, delay);
                }
            }}, delay);
    }
}