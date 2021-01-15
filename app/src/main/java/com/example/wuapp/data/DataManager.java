package com.example.wuapp.data;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.wuapp.model.Game;
import com.example.wuapp.model.UserLoginToken;

import org.w3c.dom.DOMStringList;

import java.util.*;

/**
 * This Class manages all data that is downloaded or needs downloading
 */
public class DataManager implements Parcelable {

    public static final String REQUEST_SCHEDULED_GAMES = "request_scheduled_games";
    public static final String REQUEST_RECENT_GAMES = "request_recent_games";


    private Set<Game> gameSet = new HashSet<>();
    private Queue<Request> requestQueue;

    private UserLoginToken loginToken;


    public DataManager(){
        gameSet.add(new Game.Builder().setHomeTeamName("HOME TEAM NAME!").setAwayTeamName("AWAY TEAM").build());
    }

    protected DataManager(Parcel in) {
        gameSet.addAll(in.createTypedArrayList(Game.CREATOR));
    }

    private void processQueue(){
        Handler handler = new Handler();
        int delay = 500; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(!requestQueue.isEmpty()){
                    for(Request r : requestQueue){
                        processRequest(r);
                    }
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void processRequest(Request r){
        ArrayList<Game> results = new ArrayList<>();

        switch (r.request){
            case DataManager.REQUEST_SCHEDULED_GAMES:
                for(Game g : gameSet){
                    if(g.isUpcoming()){ results.add(g); }
                }
                break;
        }

        r.callback.DataReady(results);
    }

    public static final Creator<DataManager> CREATOR = new Creator<DataManager>() {
        @Override
        public DataManager createFromParcel(Parcel in) {
            return new DataManager(in);
        }

        @Override
        public DataManager[] newArray(int size) {
            return new DataManager[size];
        }
    };

    public void makeRequest(DataReady callback, String request){
        requestQueue.add(new Request(callback, request));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        List<Game> gameList = new ArrayList<>();
        gameList.addAll(gameSet);
        parcel.writeTypedList(gameList);
    }


    private class Request{

        public final String request;
        public final DataReady callback;

        public Request(DataReady callback, String request){
            this.request = request;
            this.callback = callback;
        }

    }

    @Override
    public String toString() {
        return "DataManager{" +
                "gameSet=" + gameSet +
                ", requestQueue=" + requestQueue +
                '}';
    }
}


