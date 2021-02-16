package com.example.wuapp.data;

import com.example.wuapp.model.Event;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.ReportFormState;
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

    /**
     * This function is used to parse a report form page into its respective ReportFormState object
     *
     * In a typical report form the spinners are in order of:
     * 1.home team score 2. away team score 3. RKU, FBC, FM, PAS, COM spinners
     * 3. mvp spinners starting with females first.
     *
     * @param doc This is a JSoup Document representing a report form webpage
     * @return A ReportFormState Object representing the given Document/page
     */
    public static ReportFormState parseReportForm(Document doc) {
            String comments = null;

            ReportFormState.spinnerState homeScoreSpinner, awayScoreSpinner, RKUSpinner,
                    FBCSpinner, FMSpinner, PASSpinner, COMSpinner;
            List<ReportFormState.spinnerState> maleMVPSpinners = new ArrayList<>(),
                    femaleMVPSpinners = new ArrayList<>();

            int selectFormIndex = 0;
            List<Element> selectForms = doc.getElementsByTag("select");

            homeScoreSpinner = parseSpinner(selectForms.get(selectFormIndex++));
            awayScoreSpinner = parseSpinner(selectForms.get(selectFormIndex++));
            RKUSpinner = parseSpinner(selectForms.get(selectFormIndex++));
            FBCSpinner = parseSpinner(selectForms.get(selectFormIndex++));
            FMSpinner = parseSpinner(selectForms.get(selectFormIndex++));
            PASSpinner = parseSpinner(selectForms.get(selectFormIndex++));
            COMSpinner = parseSpinner(selectForms.get(selectFormIndex++));

            int mvpCount = selectForms.size() - selectFormIndex;
            while (selectFormIndex < selectForms.size()) {
                if (selectFormIndex - 7 < mvpCount / 2) {
                    femaleMVPSpinners.add(parseSpinner(selectForms.get(selectFormIndex++)));
                } else {
                    maleMVPSpinners.add(parseSpinner(selectForms.get(selectFormIndex++)));
                }
            }

            Element commentsElem = (doc.getElementById("game_home_game_report_survey_6_answer"));
            if (commentsElem == null) {
                commentsElem = doc.getElementById("game_away_game_report_survey_6_answer");
            }
            comments = commentsElem.text();

            return new ReportFormState.Builder()
                    .setHomeScoreSpinner(homeScoreSpinner)
                    .setAwayScoreSpinner(awayScoreSpinner)
                    .setRKUSpinner(RKUSpinner)
                    .setFBCSpinner(FBCSpinner)
                    .setFMSpinner(FMSpinner)
                    .setPASSpinner(PASSpinner)
                    .setCOMSpinner(COMSpinner)
                    .setFemaleMVPSpinners(femaleMVPSpinners)
                    .setMaleMVPSpinners(maleMVPSpinners)
                    .setComments(comments)
                    .setDocument(doc)
                    .build();
    }

    private static ReportFormState.spinnerState parseSpinner(Element selectTag){
        List<String> options = new ArrayList<>();
        int selectedIndex = -1, currentIndex = 0;
        for(Element option : selectTag.children()){
            options.add(option.text());
            if(option.attr("selected").equals("selected")){
                selectedIndex = currentIndex;
            }
            currentIndex++;
        }

        System.out.println(options);
        System.out.println(selectedIndex);

        return new ReportFormState.spinnerState(options, selectedIndex);
    }

}
