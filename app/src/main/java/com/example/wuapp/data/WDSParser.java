package com.example.wuapp.data;

import com.example.wuapp.model.Event;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.Team;
import com.example.wuapp.model.WebLoader;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

public class WDSParser {

    public static Set<Event> parseEvents(Document htmlDOC) {
        Set<Event> results = new HashSet<>();
        String eventName, eventImg, eventLink;

        Elements eventLinks = htmlDOC.getElementsByClass("global-toolbar-subnav-img-item plain-link");
        for (Element e : eventLinks) {
            if (e.attr("href").startsWith("/e/")) {
                eventLink = htmlDOC.location() + e.attr("href");
            } else {
                continue;
            }

            //Collect event image url from page
            Element eventImgElem = e.child(0);
            eventImg = eventImgElem.attr("src");

            //Collect event name from page
            Element eventNameElem = e.child(1);
            eventName = eventNameElem.text();

            results.add(new Event(eventName, eventImg, eventLink));
        }
        return results;
    }

    public static Set<Team> parseTeams(Document htmlDOC){
        Set<Team> teams = new HashSet<>();
        for (Element teamDiv : htmlDOC.getElementsByClass("span4 media-item-wrapper spacer1 ")) {

            String teamName, teamImage;
            teamName = teamDiv.getElementsByTag("h3").first().text();
            teamImage = teamDiv.getElementsByClass("media-item-tile media-item-tile-normal media-item-tile-cover").first().attr("style");
            teamImage = teamImage.substring(23, teamImage.length() - 2);

            List<String> maleMatchups = new ArrayList<>();
            List<String> femaleMatchups = new ArrayList<>();

            for (Element genderMatchupDiv : teamDiv.getElementsByClass("gender-cluster")) {
                List<String> matchups = new ArrayList<>();
                for (Element member : genderMatchupDiv.getElementsByTag("a")) {
                    if (member.nextElementSibling().children().first().attr("data-value").contains("player") ||
                            member.nextElementSibling().children().first().attr("data-value").contains("captain")) { //check they are a player not a coach/admin
                        matchups.add(member.text().trim());
                    }
                }

                if (genderMatchupDiv.getElementsByTag("h5").first().text().startsWith("Female")) {
                    femaleMatchups = matchups;
                } else {
                    maleMatchups = matchups;
                }
            }
            teams.add(new Team(teamName, teamImage, maleMatchups, femaleMatchups));
        }
        return teams;
    }

    public static Set<Game> parseGames(Document htmlDOC){

        Set<Game> results = new HashSet<>();

        System.out.println(htmlDOC.location() + " " + htmlDOC.getElementsByClass("game-list-item").size());
        for(Element gameDiv : htmlDOC.getElementsByClass("game-list-item")){
            try {
                String homeTeamName, homeTeamImg, awayTeamName, awayTeamImg, Event, date, time, location,
                        homeTeamScore = "?", homeTeamSpirit = "?", awayTeamScore = "?", awayTeamSpirit = "?", reportLink = null;

                //find divs containing team info
                Element homeTeamElem = gameDiv.getElementsByClass("game-participant").first();
                Element awayTeamElem = gameDiv.getElementsByClass("game-participant").last();

                Element scoreBoxDiv = gameDiv.getElementsByClass("schedule-score-box").first();
                if (scoreBoxDiv != null) { //Score is reportable
                    if (scoreBoxDiv.classNames().contains("can-report")) {
                        Element reportLinkTag = scoreBoxDiv.getElementsByTag("a").first();
                        reportLink = reportLinkTag.attr("href");
                    }
                    if (scoreBoxDiv.classNames().contains("with-score")) { //check if score has been reported
                        Element homeTeamScoreElem = homeTeamElem.getElementsByClass("score ").first();
                        homeTeamScore = ((homeTeamScoreElem == null) ? "?" : homeTeamScoreElem.text().trim().replaceAll("(--)", "?"));

                        Element homeTeamSpiritElem = homeTeamElem.getElementsByClass("schedule-score-box-game-result").first();
                        homeTeamSpirit = ((homeTeamSpiritElem == null) ? "?" : homeTeamSpiritElem.text().trim().replaceAll("[^0-9?]", ""));

                        Element awayTeamScoreElem = awayTeamElem.getElementsByClass("score ").first();
                        awayTeamScore = ((awayTeamScoreElem == null) ? "?" : awayTeamScoreElem.text().trim().replaceAll("(--)", "?"));

                        Element awayTeamSpiritElem = awayTeamElem.getElementsByClass("schedule-score-box-game-result").first();
                        awayTeamSpirit = ((awayTeamSpiritElem == null) ? "?" : awayTeamSpiritElem.text().trim().replaceAll("[^0-9?]", ""));
                    }
                }

                //Team Names and Images
                Element homeTeamNameElem = homeTeamElem.getElementsByClass("schedule-team-name ").first();
                homeTeamName = homeTeamNameElem.text().trim();

                Element homeTeamImgElem = homeTeamElem.child(0);
                homeTeamImg = homeTeamImgElem.attr("src");

                Element awayTeamNameElem = awayTeamElem.getElementsByClass("schedule-team-name ").first();
                awayTeamName = awayTeamNameElem.text().trim();

                Element awayTeamImgElem = awayTeamElem.child(0);
                awayTeamImg = awayTeamImgElem.attr("src");

                //Time and Date
                Element datetimeElem = gameDiv.getElementsByClass("clearfix").first();
                String[] datetime = datetimeElem.text().trim().split(" ");

                date = datetime[0] + " " + datetime[1];
                time = datetime[2] + " " + datetime[3];

                //Location and Event
                Element locationLeagueElem = gameDiv.getElementsByClass("clearfix").last();
                Element locationElem = locationLeagueElem.getElementsByClass("push-left").first();
                location = locationElem.text().trim();

                Element LeagueElem = locationLeagueElem.getElementsByClass("push-right").first();
                Event = LeagueElem.text().trim();

                results.add(new Game.Builder()
                        .setHomeTeamName(homeTeamName)
                        .setHomeTeamImg(homeTeamImg)
                        .setHomeTeamScore(homeTeamScore)
                        .setHomeTeamSpirit(homeTeamSpirit)
                        .setAwayTeamName(awayTeamName)
                        .setAwayTeamImg(awayTeamImg)
                        .setAwayTeamScore(awayTeamScore)
                        .setAwayTeamSpirit(awayTeamSpirit)
                        .setLeague(Event)
                        .setDate(date)
                        .setTime(time)
                        .setLocation(location)
                        .setReportLink(reportLink)
                        .build());
            } catch (Exception e) {
                continue;
            }
        }
        return results;
    }

}
