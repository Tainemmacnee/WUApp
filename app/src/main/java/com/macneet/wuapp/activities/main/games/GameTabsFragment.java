package com.macneet.wuapp.activities.main.games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.macneet.wuapp.R;
import com.macneet.wuapp.datamanagers.APIGameManager;
import com.macneet.wuapp.datamanagers.DataReceiver;
import com.macneet.wuapp.model.Game;
import com.macneet.wuapp.activities.main.DisplayFragment;
import com.google.android.material.tabs.TabLayout;

public class GameTabsFragment extends Fragment {

    DisplayFragment<Game> selectedFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gametabs, container, false);

        TabLayout tabLayout = v.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    selectedFragment = new UpcomingGamesFragment();
                    APIGameManager.getInstance().requestData(new DataReceiver.Request(selectedFragment, APIGameManager.REQUEST_SCHEDULED_GAMES));
                }
                if(tab.getPosition() == 1){
                    selectedFragment = new PlayedGamesFragment();
                    APIGameManager.getInstance().requestData(new DataReceiver.Request(selectedFragment, APIGameManager.REQUEST_RECENT_GAMES));
                }
                loadFragment(selectedFragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });
        tabLayout.selectTab(tabLayout.getTabAt(0));

        return v;
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.tabLayout_framelayout, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}