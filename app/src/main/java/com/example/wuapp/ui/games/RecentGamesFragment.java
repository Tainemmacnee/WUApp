package com.example.wuapp.ui.games;

import com.example.wuapp.MainActivity;
import com.example.wuapp.data.DataManager;
import com.example.wuapp.data.DataReceiver;

public class RecentGamesFragment extends GamesFragment {

    @Override
    public void makeRequest(DataManager dataManager, DataReceiver self, String request) {
        dataManager.makeRequest(self, DataManager.REQUEST_RECENT_GAMES);
    }
}
