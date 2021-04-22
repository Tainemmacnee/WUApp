package com.macneet.wuapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The Event class is used to represent and store the data for a users events
 */
public class Event implements Serializable, Parcelable {

    String eventName, eventImg, eventLink;

    Set<Team> eventTeams = new HashSet<>();

    protected Event(Parcel in) {
        eventName = in.readString();
        eventImg = in.readString();
        eventLink = in.readString();
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

    public String getEventLink() {
        return eventLink;
    }

    public Event(String eventName, String eventImg, String eventLink){
        this.eventName = eventName;
        this.eventImg = eventImg.replace("40", "200");
        this.eventLink = eventLink;
    }

    public void setTeams(Set<Team> teams){
        this.eventTeams = teams;
    }

    public Set<Team> getTeams(){
        return this.eventTeams;
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
        parcel.writeString(eventLink);

        List<Team> teamList = new ArrayList<>();
        teamList.addAll(eventTeams);
        parcel.writeTypedList(teamList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return eventName.equals(event.eventName) &&
                eventImg.equals(event.eventImg) &&
                eventLink.equals(event.eventLink) &&
                eventTeams.equals(event.eventTeams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventName, eventImg, eventLink);
    }

}
