package com.macneet.wuapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * The Team class is used to represent and store the data for a team that is part of an event
 */
public class Team implements Serializable, Parcelable {

    private String name, imageUrl;
    private List<String> maleMatchups, femaleMatchups;

    public Team(String name, String imageUrl, List<String> maleMatchups, List<String> femaleMatchups) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.maleMatchups = maleMatchups;
        this.femaleMatchups = femaleMatchups;
    }

    protected Team(Parcel in) {
        name = in.readString();
        imageUrl = in.readString();
        maleMatchups = in.createStringArrayList();
        femaleMatchups = in.createStringArrayList();
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    public String getName() { return this.name; }

    public String getImageUrl() { return imageUrl; }

    public List<String> getMaleMatchups(){ return this.maleMatchups; }

    public List<String> getFemaleMatchups(){ return this.femaleMatchups; }

    public String toString(){
        return "TEAM: "+this.name+" IMAGE: "+this.imageUrl+" MEMBERS "+this.maleMatchups+" "+this.femaleMatchups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return name.equals(team.name) &&
                imageUrl.equals(team.imageUrl) &&
                maleMatchups.equals(team.maleMatchups) &&
                femaleMatchups.equals(team.femaleMatchups);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + name.hashCode();
        hash = 31 * hash + (imageUrl == null ? 0 : imageUrl.hashCode());
        hash = 31 * hash + (maleMatchups == null ? 0 : maleMatchups.hashCode());
        hash = 31 * hash + (femaleMatchups == null ? 0 : femaleMatchups.hashCode());
        return hash;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(imageUrl);
        parcel.writeStringList(maleMatchups);
        parcel.writeStringList(femaleMatchups);
    }
}
