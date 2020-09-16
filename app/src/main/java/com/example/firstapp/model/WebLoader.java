package com.example.firstapp.model;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WebLoader {

    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static final String WEB_URL = "https://wds.usetopscore.com";

    public static Document loadWebPage(Map<String, String> cookies, String link){
        Document result = null;
        try {
            Connection.Response loadPageResponse = Jsoup.connect(link)
                    .method(Connection.Method.GET)
                    .userAgent(User.USER_AGENT)
                    .cookies(cookies)
                    .execute();
            result = loadPageResponse.parse();
        } catch (IOException e) {}
        return result;
    }

    public static Future<List<Game>> LoadUpcomingGames(Map<String, String> cookies, String link){
        List<Game> output = new ArrayList<>();

        return executor.submit(() -> {
                Document doc = loadWebPage(cookies, WEB_URL+link);

                String homeTeamName, homeTeamImg, awayTeamName, awayTeamImg, league, date, time, location;

                Elements upcomingGameBlocks = doc.getElementsByClass("striped-block");
                for(Element e: upcomingGameBlocks){
                    //Collect game data from page

                    Element datetimeElem = e.getElementsByClass("clearfix").first();
                    String[] datetime = datetimeElem.text().trim().split(" ");

                    date = datetime[0] + " " + datetime[1];
                    time = datetime[2] + " " + datetime[3];

                    Element locationLeagueElem = e.getElementsByClass("clearfix").last();
                    Element locationElem = locationLeagueElem.getElementsByClass("push-left").first();
                    location = locationElem.text().trim();

                    Element LeagueElem = locationLeagueElem.getElementsByClass("push-right").first();
                    league = LeagueElem.text().trim();

                    Element homeTeamElem = e.getElementsByClass("game-participant").first();
                    Element homeTeamNameElem = homeTeamElem.getElementsByClass("schedule-team-name").first();
                    homeTeamName = homeTeamNameElem.text().trim();

                    Element homeTeamImgElem = homeTeamElem.child(0);
                    homeTeamImg = homeTeamImgElem.attr("src");

                    Element awayTeamElem = e.getElementsByClass("game-participant").last();
                    Element awayTeamNameElem = awayTeamElem.getElementsByClass("schedule-team-name").first();
                    awayTeamName = awayTeamNameElem.text().trim();

                    Element awayTeamImgElem = awayTeamElem.child(0);
                    awayTeamImg = awayTeamImgElem.attr("src");

                    output.add(new Game(homeTeamName, homeTeamImg, awayTeamName, awayTeamImg, league, date, time, location));
                }
            return output;
        });
    }

    public static Future<List<Game>> LoadMissingResultsGames(Map<String, String> cookies, String link) {
        List<Game> output = new ArrayList<>();

        return executor.submit(() -> {
                Document doc = loadWebPage(cookies, WEB_URL+link);

                String homeTeamName, homeTeamImg, awayTeamName, awayTeamImg, league, date, time, location,
                        homeTeamScore, homeTeamSpirit, awayTeamScore, awayTeamSpirit, reportLink;

                Elements upcomingGameBlocks = doc.getElementsByClass("striped-block");
                for(Element gameBlock: upcomingGameBlocks){
                    //Collect game data from page

                    Element scoreBoxDiv = gameBlock.getElementsByClass("schedule-score-box").first();
                    Element reportLinkTag = scoreBoxDiv.getElementsByTag("a").first();

                    if(reportLinkTag == null){ //game is not reportable, so skip
                        continue;
                    }

                    reportLink = reportLinkTag.attr("href");

                    Element datetimeElem = gameBlock.getElementsByClass("clearfix").first();
                    String[] datetime = datetimeElem.text().trim().split(" ");

                    date = datetime[0] + " " + datetime[1];
                    time = datetime[2] + " " + datetime[3];

                    Element locationLeagueElem = gameBlock.getElementsByClass("clearfix").last();
                    Element locationElem = locationLeagueElem.getElementsByClass("push-left").first();
                    location = locationElem.text().trim();

                    Element LeagueElem = locationLeagueElem.getElementsByClass("push-right").first();
                    league = LeagueElem.text().trim();

                    Element homeTeamElem = gameBlock.getElementsByClass("game-participant").first();
                    Element homeTeamNameElem = homeTeamElem.getElementsByClass("schedule-team-name ").first();
                    homeTeamName = homeTeamNameElem.text().trim();

                    Element homeTeamScoreElem = homeTeamElem.getElementsByClass("score ").first();
                    if(homeTeamScoreElem == null) {homeTeamScore = "?";} else{
                        homeTeamScore = homeTeamScoreElem.text().trim();
                    }

                    Element homeTeamSpiritElem = homeTeamElem.getElementsByClass("schedule-score-box-game-result").first();
                    if(homeTeamSpiritElem == null) {homeTeamSpirit = "?";} else {
                        homeTeamSpirit = homeTeamSpiritElem.text().trim();
                    }

                    Element homeTeamImgElem = homeTeamElem.child(0);
                    homeTeamImg = homeTeamImgElem.attr("src");

                    Element awayTeamElem = gameBlock.getElementsByClass("game-participant").last();
                    Element awayTeamNameElem = awayTeamElem.getElementsByClass("schedule-team-name ").first();
                    awayTeamName = awayTeamNameElem.text().trim();

                    Element awayTeamScoreElem = awayTeamElem.getElementsByClass("score ").first();
                    if(awayTeamScoreElem == null) {awayTeamScore = "?";} else{
                        awayTeamScore = awayTeamScoreElem.text().trim();
                    }

                    Element awayTeamSpiritElem = awayTeamElem.getElementsByClass("schedule-score-box-game-result").first();
                    if(awayTeamSpiritElem == null) {awayTeamSpirit = "?";} else {
                        awayTeamSpirit = awayTeamSpiritElem.text().trim();
                    }

                    Element awayTeamImgElem = awayTeamElem.child(0);
                    awayTeamImg = awayTeamImgElem.attr("src");

                    Game game = new Game(homeTeamName, homeTeamImg, awayTeamName, awayTeamImg, league, date, time, location);
                    game.homeTeamScore = homeTeamScore;
                    game.awayTeamScore = awayTeamScore;
                    game.homeTeamSpirit = homeTeamSpirit;
                    game.awayTeamSpirit = awayTeamSpirit;
                    game.reportLink = reportLink;
                    output.add(game);
                }
            return output;
        });
    }

    public static Future<List<Event>> LoadEvents(Map<String, String> cookies){
        List<Event> output = new ArrayList<>();

        return executor.submit(() -> {
                Document doc = loadWebPage(cookies, WEB_URL);

                String eventName, eventImg, standingsLink;
                Future<List<Team>> eventTeams;

                Elements eventLinks = doc.getElementsByClass("global-toolbar-subnav-img-item plain-link");
                for(Element e: eventLinks){
                    if(e.attr("href").startsWith("/e/")) {
                        eventTeams = WebLoader.loadTeams(e.attr("href"), cookies);
                        standingsLink = WEB_URL+e.attr("href");
                    } else {
                        continue;
                    }

                    //Collect event image url from page
                    Element eventImgElem = e.child(0);
                    eventImg = eventImgElem.attr("src");

                    //Collect event name from page
                    Element eventNameElem = e.child(1);
                    eventName = eventNameElem.text();

                    output.add(new Event(eventName, eventImg, eventTeams, standingsLink, cookies));
                }
            return output;
        });
    }

    public static Future<List<Team>> loadTeams(String eventTeamUrl, Map<String, String> cookies){
        List<Future<List<Team>>> futures = new ArrayList<>();

        return executor.submit(() -> {
            List<Team> teams = new ArrayList<>();
            for(int i = 1; i < 6; i++){
                futures.add(executor.submit(new WebLoader.loadTeamsTask(WEB_URL + eventTeamUrl + "/teams?page=" + i, cookies)));
            }

            for(Future f : futures){
                teams.addAll((Collection<? extends Team>) f.get());
            }

            return teams;
        });
        //Load the first 5 pages of teams (THIS SHOUlD BE ALL OF THEM (MAX 70TEAMS LOL))
    }

    private static class loadTeamsTask implements Callable<List<Team>> {

        private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        private String URL;
        private Map<String, String> cookies;

        public loadTeamsTask(String URL, Map<String, String> cookies){
            this.URL = URL;
            this.cookies = cookies;
        }

        @Override
        public List<Team> call() throws Exception {
            Connection.Response loadPageResponse = Jsoup.connect(URL)
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .cookies(cookies)
                    .execute();
            Document doc = loadPageResponse.parse();

            List<Team> teams = new ArrayList<>();
            for(Element teamDiv : doc.getElementsByClass("span4 media-item-wrapper spacer1 ")){
                String teamName, teamImage;

                teamName = teamDiv.getElementsByTag("h3").first().text();
                teamImage = teamDiv.getElementsByClass("media-item-tile media-item-tile-normal media-item-tile-cover").first().attr("style");
                teamImage = teamImage.substring(23, teamImage.length()-2);

                List<String> maleMatchups = new ArrayList<>();
                List<String> femaleMatchups = new ArrayList<>();

                for(Element genderMatchupDiv : teamDiv.getElementsByClass("gender-cluster")){
                    List<String> matchups = new ArrayList<>();
                    for(Element member : genderMatchupDiv.getElementsByTag("a")){
                        if(member.nextElementSibling().children().first().attr("data-value").contains("player")) { //check they are a player not a coach/admin
                            matchups.add(member.text().trim());
                        }
                    }

                    if(genderMatchupDiv.getElementsByTag("h5").first().text().startsWith("Female")){
                        femaleMatchups = matchups;
                    } else {
                        maleMatchups = matchups;
                    }
                }

                teams.add(new Team(teamName, teamImage, maleMatchups, femaleMatchups));
            }
            return teams;
        }
    }

    public static Future<?> loadUser(UserLoginToken loginToken) {
        HashMap<String, String> links = loginToken.getLinks();
        HashMap<String, String> cookies = loginToken.getCookies();

        return executor.submit(() -> {
            String NEW_URL = WEB_URL + links.get(User.USERPAGELINK);

            Map<String, String> profileInfo = new HashMap<String, String>();
            String name = "", profileImgUrl = "", aboutText = "";

            Document doc = loadWebPage(cookies, NEW_URL);

                //Collect username from page
                Element profileNameDiv = doc.getElementsByClass("profile-name").first();
                Element profileName = profileNameDiv.child(0);
                name = profileName.text().trim();

                //collect profile info
                Element profileInfoElem = doc.getElementsByClass("profile-info").first().child(0);
                for(int i = 0; i < profileInfoElem.children().size(); i++){
                    profileInfo.put(profileInfoElem.child(i).text(), profileInfoElem.child(++i).text());
                }

                //Collect user image url from page
                Element profilePicDiv = doc.getElementsByClass("profile-image").first();
                Element profilePic = profilePicDiv.child(0);
                profileImgUrl = profilePic.attr("src");

                Element profileAboutDiv = doc.getElementsByClass("profile-about").first();
                Element profileAboutText = profileAboutDiv.getElementsByClass("rich-text").first();
                aboutText = profileAboutText.text();

            return new User(loginToken, name, profileImgUrl, aboutText, profileInfo);
        });
    }
}
