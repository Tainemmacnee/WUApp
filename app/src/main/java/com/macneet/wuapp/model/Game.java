package com.macneet.wuapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

/**
 * The Game class is used to represent and store the data for a Game
 */
public class Game implements Parcelable {

    private String homeTeamName, homeTeamImg, awayTeamName, awayTeamImg, league, date, time,
            location, homeTeamScore, homeTeamSpirit, awayTeamScore, awayTeamSpirit, reportLink;

    protected Game(Parcel in) {
        homeTeamName = in.readString();
        homeTeamImg = in.readString();
        awayTeamName = in.readString();
        awayTeamImg = in.readString();
        league = in.readString();
        date = in.readString();
        time = in.readString();
        location = in.readString();
        homeTeamScore = in.readString();
        homeTeamSpirit = in.readString();
        awayTeamScore = in.readString();
        awayTeamSpirit = in.readString();
        reportLink = in.readString();
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    public String getHomeTeamName() { return homeTeamName; }

    public String getHomeTeamImg() { return homeTeamImg; }

    public String getHomeTeamScore() { return homeTeamScore; }

    public String getHomeTeamSpirit() { return homeTeamSpirit; }

    public String getDate() { return date; }

    public String getTime() { return time; }

    public String getLeague() { return league; }

    public String getAwayTeamName() { return awayTeamName; }

    public String getAwayTeamImg() { return awayTeamImg; }

    public String getAwayTeamScore() { return awayTeamScore; }

    public String getAwayTeamSpirit() { return awayTeamSpirit; }

    public String getLocation() { return location; }

    public String getReportLink() { return reportLink; }

    public boolean isReportable() { return reportLink != null; }

    public boolean isUpcoming(){
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd/MM/yyyy hh:mm a");
        Date gameDate = new Date();
        try {
            gameDate = sdf.parse(String.format("%s %s", date, time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return gameDate.after(new Date());
    }

    public boolean hasScores(){
        return !homeTeamScore.equals("?") && !awayTeamScore.equals("?");
    }

    public String toString(){
        return String.format("%s vs %s @%s %s", homeTeamName, awayTeamName, date, time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return homeTeamName.equals(game.homeTeamName) &&
                awayTeamName.equals(game.awayTeamName) &&
                league.equals(game.league) &&
                date.equals(game.date) &&
                time.equals(game.time) &&
                location.equals(game.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(homeTeamName, awayTeamName, league, date, time, location);
    }

    private Game(Builder builder){
        this.homeTeamName = builder.homeTeamName;
        this.homeTeamImg = builder.homeTeamImg;
        this.homeTeamSpirit = builder.homeTeamSpirit;
        this.homeTeamScore = builder.homeTeamScore;
        this.awayTeamName = builder.awayTeamName;
        this.awayTeamImg = builder.awayTeamImg;
        this.awayTeamSpirit = builder.awayTeamSpirit;
        this.awayTeamScore = builder.awayTeamScore;
        this.league = builder.league;
        this.time = builder.time;
        this.date = builder.date;
        this.location = builder.location;
        this.reportLink = builder.reportLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(homeTeamName);
        parcel.writeString(homeTeamImg);
        parcel.writeString(awayTeamName);
        parcel.writeString(awayTeamImg);
        parcel.writeString(league);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(location);
        parcel.writeString(homeTeamScore);
        parcel.writeString(homeTeamSpirit);
        parcel.writeString(awayTeamScore);
        parcel.writeString(awayTeamSpirit);
        parcel.writeString(reportLink);
    }

    public static class Builder {
        private String homeTeamName, homeTeamImg, awayTeamName, awayTeamImg, league, date, time,
                location, homeTeamScore, homeTeamSpirit, awayTeamScore, awayTeamSpirit, reportLink;

        public Builder setHomeTeamName(String homeTeamName){ this.homeTeamName = homeTeamName; return this; }

        public Builder setHomeTeamImg(String homeTeamImg){ this.homeTeamImg = homeTeamImg; return this; }

        public Builder setHomeTeamScore(String homeTeamScore){ this.homeTeamScore = homeTeamScore; return this; }

        public Builder setHomeTeamSpirit(String homeTeamSpirit){ this.homeTeamSpirit = homeTeamSpirit; return this; }

        public Builder setAwayTeamName(String awayTeamName){ this.awayTeamName = awayTeamName; return this; }

        public Builder setAwayTeamImg(String awayTeamImg){ this.awayTeamImg = awayTeamImg; return this; }

        public Builder setAwayTeamScore(String awayTeamScore){ this.awayTeamScore = awayTeamScore; return this; }

        public Builder setAwayTeamSpirit(String awayTeamSpirit){ this.awayTeamSpirit = awayTeamSpirit; return this; }

        public Builder setLeague(String league){ this.league = league; return this; }

        public Builder setDate(String date){ this.date = date; return this; }

        public Builder setTime(String time){ this.time = time; return this; }

        public Builder setLocation(String location){ this.location = location; return this; }

        public Builder setReportLink(String reportLink){ this.reportLink = reportLink; return this; }

        public Game build(){
            return new Game(this);
        }
    }

    public static class SortByMostRecentDate implements Comparator<Game>{

        @Override
        public int compare(Game game1, Game game2) {
            SimpleDateFormat sdf = new SimpleDateFormat("E, dd/MM/yyyy hh:mm a");
            try {
                Date gameDate1 = sdf.parse(String.format("%s %s", game1.date, game1.time));
                Date gameDate2 = sdf.parse(String.format("%s %s", game2.date, game2.time));

                return gameDate1.before(gameDate2) ? -1 : 1;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public static class SortByLeastRecentDate implements Comparator<Game>{

        @Override
        public int compare(Game game1, Game game2) {
            SimpleDateFormat sdf = new SimpleDateFormat("E, dd/MM/yyyy hh:mm a");
            try {
                Date gameDate1 = sdf.parse(String.format("%s %s", game1.date, game1.time));
                Date gameDate2 = sdf.parse(String.format("%s %s", game2.date, game2.time));

                return gameDate1.before(gameDate2) ? 1 : -1;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
