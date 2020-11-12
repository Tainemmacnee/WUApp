package com.example.wuapp.ui.missingresultgames;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
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

public class MissingResultGamesFragment extends Fragment implements RefreshableFragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_missing_result_games, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.missing_result_recycler_view);

        DisplayUserActivity activity = (DisplayUserActivity)getActivity();
        user = activity.getUserData();

        loadDisplay(v);

        return v;
    }

    private void loadDisplay(View v){
        Game[] missingResultGames = user.getMissingResultGamesAsArray();
        if(missingResultGames.length == 0){
            TextView textView = v.findViewById(R.id.empty_events_text); //display text showing no games
            textView.setVisibility(View.VISIBLE);
        } else {
            TextView textView = v.findViewById(R.id.empty_events_text);
            textView.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);

            layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);

            mAdapter = new MissingResultAdapter(missingResultGames);
            recyclerView.setAdapter(mAdapter);
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
        recyclerView.setAdapter(new MissingResultAdapter(new Game[0])); //clear current displayed events
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