package com.example.Wuapp.model;

import java.io.Serializable;
import java.util.List;

/**
 * The Team class is used to represent and store the data for a team that is part of an event
 */
public class Team implements Serializable {

    private String name, imageUrl;
    private List<String> maleMatchups, femaleMatchups;

    public Team(String name, String imageUrl, List<String> maleMatchups, List<String> femaleMatchups) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.maleMatchups = maleMatchups;
        this.femaleMatchups = femaleMatchups;
    }

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
}
