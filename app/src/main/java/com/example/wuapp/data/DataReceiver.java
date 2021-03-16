package com.example.wuapp.data;

import java.util.ArrayList;

public interface DataReceiver {

    public <T> void receiveData(ArrayList<T> results);

    public default void makeRequest(DataManager dataManager, DataReceiver self, String request) {
        dataManager.makeRequest(self, request);
    }

}
