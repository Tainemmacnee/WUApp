package com.macneet.wuapp.parsers;

import com.macneet.wuapp.datamanagers.DataManager;
import com.macneet.wuapp.exceptions.ElementNotFoundException;
import com.macneet.wuapp.model.Event;
import com.macneet.wuapp.model.Game;
import com.macneet.wuapp.model.ReportFormState;
import com.macneet.wuapp.model.Team;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WDSParser {

    public static Set<Event> parseEvents(Document htmlDOC) throws ParseException {
        Set<Event> results = new HashSet<>();
        String eventName, eventImg, eventLink;
        try {
            Elements eventLinks = htmlDOC.getElementsByClass("global-toolbar-subnav-img-item plain-link");
            for (Element e : eventLinks) {
                if (e.attr("href").startsWith("/e/")) {
                    eventLink = DataManager.HOME_URL + e.attr("href");
                    if(eventLink.endsWith("/register")){ //remove register suffix if present
                        eventLink = eventLink.substring(0, eventLink.length() - 9);
                    }
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
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
        return results;
    }

    public static Set<Team> parseTeams(Document htmlDOC) throws ParseException {
        Set<Team> teams = new HashSet<>();
        try {
            Element list = htmlDOC.getElementsByClass("filter-list").first();
            for(Element row : list.getElementsByClass("row-fluid media-list-row")){
                for(Element teamDiv : row.children()){
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParseException(e.getMessage(), 0);
        }
        return teams;
    }


    public static Set<Game> parseGames(@NotNull Document htmlDOC) throws ParseException {
        Set<Game> results = new HashSet<>();
        try {
            for(Element gameDiv : htmlDOC.getElementsByClass("game-list-item")){
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
                if(locationElem == null) { location = "No Location"; }
                else { location = locationElem.text().trim(); }


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
            }
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
        return results;
    }

    /**
     * Function to parse standings from a wds events standings page. e.g. /e/<eventname>/standings
     *
     * @param doc the html doc to parse the standing from
     * @return a list of maps containing the required data
     * @return null if an error occurs while parsing
     */
    public static List<Map<String, String>> parseStandings(Document doc) throws ParseException, ElementNotFoundException {
        ArrayList<Map<String, String>> output = new ArrayList<>();
        try {
            Element table = doc.getElementsByClass("filter-list").first();
            Element list = table.getElementsByClass("striped-blocks spacer1").first();
            list.text(); //this will cause an intentional null pointer if list is null
            try {
                for (Element team : list.children()) {
                    HashMap<String, String> info = new HashMap<>();
                    Element img = team.getElementsByTag("img").first();
                    info.put("image", img.attr("src"));

                    Element name = team.getElementsByTag("a").first();
                    info.put("name", name.text());

                    Element record = team.getElementsByClass("plain-link plain-link").first();
                    info.put("record", record.text());

                    Element spirit = team.getElementsByClass("plain-link plain-link").last();
                    if (spirit.text().length() == 2) {
                        info.put("spirit", spirit.text() + ".00");
                    } else if (spirit.text().length() == 4) {
                        info.put("spirit", spirit.text() + "0");
                    } else {
                        info.put("spirit", spirit.text());
                    }

                    Element pointDiff = team.getElementsByClass("row-fluid-always").last().child(0);
                    info.put("pointDiff", pointDiff.text());
                    output.add(info);
                }

            } catch (Exception e) {
                throw new ParseException(e.getMessage(), 0);
            }
        } catch (ParseException e){
            throw e;
        } catch (NullPointerException e){
            throw new ElementNotFoundException();
        }
        return output;
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
    public static ReportFormState parseReportForm(Document doc) throws ParseException {
            String comments = null;

            ReportFormState.spinnerState homeScoreSpinner, awayScoreSpinner, RKUSpinner,
                    FBCSpinner, FMSpinner, PASSpinner, COMSpinner;
            List<ReportFormState.spinnerState> maleMVPSpinners = new ArrayList<>(),
                    femaleMVPSpinners = new ArrayList<>();
            try {
                int selectFormIndex = 0;
                boolean spiritLocked = doc.getElementsByClass("help-block").size() > 0;
                List<Element> selectForms = doc.getElementsByTag("select");
                List<Element> adminSelectForms = doc.getElementsByClass("row-fluid").get(2).child(1).getElementsByTag("select");
                selectForms.removeAll(adminSelectForms);

                homeScoreSpinner = parseSpinner(selectForms.get(selectFormIndex++), false, false);
                awayScoreSpinner = parseSpinner(selectForms.get(selectFormIndex++), false, false);
                RKUSpinner = parseSpinner(selectForms.get(selectFormIndex++), spiritLocked, true);
                FBCSpinner = parseSpinner(selectForms.get(selectFormIndex++), spiritLocked, true);
                FMSpinner = parseSpinner(selectForms.get(selectFormIndex++), spiritLocked, true);
                PASSpinner = parseSpinner(selectForms.get(selectFormIndex++), spiritLocked, true);
                COMSpinner = parseSpinner(selectForms.get(selectFormIndex++), spiritLocked, true);

                int mvpCount = selectForms.size() - selectFormIndex;
                while (selectFormIndex < selectForms.size()) {
                    if (selectFormIndex - 7 < mvpCount / 2) {
                        femaleMVPSpinners.add(parseSpinner(selectForms.get(selectFormIndex++), false, false));
                    } else {
                        maleMVPSpinners.add(parseSpinner(selectForms.get(selectFormIndex++), false, false));
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
            } catch (Exception e){
                throw new ParseException(e.getMessage(), 0);
            }
    }

    private static ReportFormState.spinnerState parseSpinner(Element selectTag, boolean locked, boolean adjust){
        List<String> options = new ArrayList<>();
        int selectedIndex = -1, currentIndex = 0;
        for(Element option : selectTag.children()){
            options.add(option.text());
            if(option.attr("selected").equals("selected")){
                if(currentIndex == 0 && adjust) { selectedIndex = 3; }
                else { selectedIndex = currentIndex; }
            }
            currentIndex++;
        }

        return new ReportFormState.spinnerState(options, selectedIndex, locked);
    }
}
