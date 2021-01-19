package com.example.wuapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * The Event class is used to represent and store the data for a users events
 */
public class Event implements Serializable, Parcelable {

    String eventName, eventImg, standingsLink;
    Map<String, String> cookies;
    Future<List<Team>> teams;

    Set<Team> eventTeams = new HashSet<>();

    protected Event(Parcel in) {
        eventName = in.readString();
        eventImg = in.readString();
        standingsLink = in.readString();
        eventTeams.addAll(in.createTypedArrayList(Team.CREATOR));
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getName() {
        return this.eventName;
    }

    public String getEventImg(){
        return this.eventImg;
    }

    public HashMap<String, String> getCookies() {
        return (HashMap<String, String>) cookies;
    }

    public String getStandingsLink() {
        return standingsLink;
    }

    public Event(String eventName, String eventImg, Future<List<Team>> teams, String standingsLink, Map<String, String> cookies)
    {
        this.teams = teams;
        this.eventName = eventName;
        this.eventImg = eventImg.replace("40", "200");
        this.standingsLink = standingsLink;
        this.cookies = cookies;
    }

    public Event(String eventName, String eventImg, String eventLink){
        this.eventName = eventName;
        this.eventImg = eventImg.replace("40", "200");
        this.standingsLink = eventLink;
    }

    public void setTeams(Set<Team> teams){
        this.eventTeams = teams;
    }

    public List<Team> getTeams(){
        List<Team> teams = new ArrayList<>();
        try {
            teams =  this.teams.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return teams;
    }

    public Team getTeam(String teamName){
        for(Team t : getTeams()){
            if(t.getName().equals(teamName)){
                return t;
            }
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(eventName);
        parcel.writeString(eventImg);
        parcel.writeString(standingsLink);

        List<Team> teamList = new ArrayList<>();
        teamList.addAll(eventTeams);
        parcel.writeTypedList(teamList);
    }
}
