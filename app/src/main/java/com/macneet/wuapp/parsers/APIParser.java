package com.macneet.wuapp.parsers;

import com.macneet.wuapp.model.Game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class APIParser {

    public static Set<Game> parseGames(JSONArray gamesJSON) {
        HashSet<Game> games = new HashSet<>();

        try {
            for (int i = 0; i < gamesJSON.length(); i++) {
                JSONObject gameJSON = gamesJSON.getJSONObject(i);
                JSONObject homeTeamJSON = gameJSON.getJSONObject("HomeTeam");
                JSONObject homeTeamImagesJSON = homeTeamJSON.getJSONObject("images");
                JSONObject awayTeamJSON = gameJSON.getJSONObject("AwayTeam");
                JSONObject awayTeamImagesJSON = awayTeamJSON.getJSONObject("images");

                String gameID = gameJSON.getString("id");
                String homeTeamName = homeTeamJSON.getString("name");
                String homeTeamID = homeTeamJSON.getString("id");
                String homeTeamImg = homeTeamImagesJSON.getString("200");
                String homeTeamScore = gameJSON.optString("home_score", "?");
                String homeTeamSpirit = gameJSON.optString("home_game_report_score", "?");
                String awayTeamName = awayTeamJSON.getString("name");
                String awayTeamID = awayTeamJSON.getString("id");
                String awayTeamImg = awayTeamImagesJSON.getString("200");
                String awayTeamScore = gameJSON.optString("away_score", "?");
                String awayTeamSpirit = gameJSON.optString("away_game_report_score", "?");
                String EventName = gameJSON.getString("event_name").trim();
                String location = gameJSON.getString("field_name");

                //correct potential null values
                homeTeamScore = homeTeamScore.equals("null") ? "?" : homeTeamScore;
                homeTeamSpirit = homeTeamSpirit.equals("null") ? "?" : homeTeamSpirit;
                awayTeamScore = awayTeamScore.equals("null") ? "?" : awayTeamScore;
                awayTeamSpirit = awayTeamSpirit.equals("null") ? "?" : awayTeamSpirit;

                //format date
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat targetFormat = new SimpleDateFormat("EEE, dd/MM/yyyy");
                Date date = originalFormat.parse(gameJSON.getString("start_date"));
                String formattedDate = targetFormat.format(date);

                //format time
                DateFormat originalTimeFormat = new SimpleDateFormat("HH:mm:ss");
                DateFormat targetTimeFormat = new SimpleDateFormat("KK:mm a");
                Date time = originalTimeFormat.parse(gameJSON.getString("start_time"));
                String formattedTime = targetTimeFormat.format(time);

                String reportLink = "https://wellington.ultimate.org.nz/admin/game/report-score/"+gameID;

                games.add(new Game.Builder()
                        .setHomeTeamName(homeTeamName)
                        .setHomeTeamImg(homeTeamImg)
                        .setHomeTeamScore(homeTeamScore)
                        .setHomeTeamSpirit(homeTeamSpirit)
                        .setAwayTeamName(awayTeamName)
                        .setAwayTeamImg(awayTeamImg)
                        .setAwayTeamScore(awayTeamScore)
                        .setAwayTeamSpirit(awayTeamSpirit)
                        .setLeague(EventName)
                        .setDate(formattedDate)
                        .setTime(formattedTime)
                        .setLocation(location)
                        .setReportLink(reportLink)
                        .build());

            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            return null;
        }
        return games;
    }
}
