package com.example.firstapp.model;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
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

/**
* The WebLoader class is used to do all of the required interaction with the website
*/
public class WebLoader {

    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static final String WEB_URL = "https://wds.usetopscore.com";

    /**
     * This function is used to load and parse a given web page, the cookies are used to
     * authenticate the user so that they can access the page.
     * @param cookies Contains the needed cookies for authentication
     * @param link This is the link the the web page to the loaded and parsed
     * @return the web page parsed as A JSoup Document
     */
    public static Document loadWebPage(Map<String, String> cookies, String link){
        Document result = null;
        long t1 = System.currentTimeMillis();
        try {
            Connection.Response loadPageResponse = Jsoup.connect(link)
                    .method(Connection.Method.GET)
                    .userAgent(User.USER_AGENT)
                    .cookies(cookies)
                    .execute();
            System.out.println("Time to load"+link+": "+(System.currentTimeMillis()-t1));
            result = loadPageResponse.parse();
        } catch (IOException e) {}
        return result;
    }

    /**
     * This function is used to scrape all of the upcoming games from the users schedule page.
     * This is done using futures so that the data can be loaded concurrently and not freeze the app.
     * @param cookies Contains the needed cookies for authentication
     * @param link This is the link to the page containing the upcoming games
     * @return A Future list of Game objects collected from the web page
     */
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

                    output.add(new Game.Builder()
                                    .setHomeTeamName(homeTeamName)
                                    .setHomeTeamImg(homeTeamImg)
                                    .setAwayTeamName(awayTeamName)
                                    .setAwayTeamImg(awayTeamImg)
                                    .setLeague(league)
                                    .setDate(date)
                                    .setTime(time)
                                    .setLocation(location)
                                    .build());
                }
            return output;
        });
    }

    /**
     * This function is used to scrape all of the games that are missing results from the users
     * schedule page. This is done using futures so that the data can be loaded concurrently
     * and not freeze the app.
     * @param cookies Contains the needed cookies for authentication
     * @param link This is the link to the page containing the games missing results
     * @return A Future list of Game objects collected from the web page
     */
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

                    output.add(new Game.Builder()
                            .setHomeTeamName(homeTeamName)
                            .setHomeTeamImg(homeTeamImg)
                            .setHomeTeamScore(homeTeamScore)
                            .setHomeTeamSpirit(homeTeamSpirit)
                            .setAwayTeamName(awayTeamName)
                            .setAwayTeamImg(awayTeamImg)
                            .setAwayTeamScore(awayTeamScore)
                            .setAwayTeamSpirit(awayTeamSpirit)
                            .setLeague(league)
                            .setDate(date)
                            .setTime(time)
                            .setLocation(location)
                            .setReportLink(reportLink)
                            .build());


//                    Game game = new Game(homeTeamName, homeTeamImg, awayTeamName, awayTeamImg, league, date, time, location);
//                    game.homeTeamScore = homeTeamScore;
//                    game.awayTeamScore = awayTeamScore;
//                    game.homeTeamSpirit = homeTeamSpirit;
//                    game.awayTeamSpirit = awayTeamSpirit;
//                    game.reportLink = reportLink;
//                    output.add(game);
                }
            return output;
        });
    }

    /**
     * This function is used to scrape all of the users events from the home page. This is done
     * using futures so that the data can be loaded concurrently and not freeze the app.
     * @param cookies Contains the needed cookies for authentication
     * @return A Future list of Event objects collected from the web page
     */
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

    /**
     * This function is used to collect all of the teams on the first 5 pages from a given event.
     * This function uses the loadTeamsTask to load each page concurrently.
     * This is done using futures so that the data can be loaded concurrently and not freeze the app.
     * @param eventTeamUrl This is the link to the event page
     * @param cookies Contains the needed cookies for authentication
     * @return A Future list of Team objects collected from the first 5 pages of teams
     */
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

    /**
     * This class is a helper class for loadTeams that is used to load team objects from a web page
     */
    private static class loadTeamsTask implements Callable<List<Team>> {

        private String URL;
        private Map<String, String> cookies;

        public loadTeamsTask(String URL, Map<String, String> cookies){
            this.URL = URL;
            this.cookies = cookies;
        }

        /**
         * This function is used to execute the task of loading the teams from URL
         * @return a List of Team objects taken from the web page
         */
        @Override
        public List<Team> call(){
            Document doc = loadWebPage(cookies, URL);

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

    /**
     * This function is used to load some important user related information e.g. Name,
     * profile image, ect. This is done using futures so that the data can be loaded concurrently
     * and not freeze the app.
     * @param loginToken This is the token created when the user logs in
     * @return A Future User object
     */
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
                aboutText = profileAboutText != null ? profileAboutText.text() : null;

            return new User(loginToken, name, profileImgUrl, aboutText, profileInfo);
        });
    }

    /**
     * This function uses the websites api to generate an OAuth2 authentication token that is needed
     * for any api requests. This is done using futures so that the data can be loaded concurrently
     * and not freeze the app.
     * @param cookies Contains the needed cookies for authentication
     * @return a Future String representation of an OAuth2 token
     */
    public static Future<String> getOAuthToken(Map<String, String> cookies) {
        final String WEB_URL = "https://wds.usetopscore.com/u/oauth-key";

        return executor.submit(() -> {
            List<String> credentials = executor.submit(() -> {
                List<String> output = new ArrayList<>();

                Document doc = loadWebPage(cookies, WEB_URL);
                Element table = doc.getElementsByClass("table no-border").first();

                for(Element row : table.getElementsByTag("tr")){ //find id and secret
                    output.add(row.getElementsByTag("td").first().text());
                }

                output.remove(2); //remove useless string

                return  output;
            }).get();

            String output = (String) executor.submit(() -> {
                Document response = Jsoup.connect("https://wds.usetopscore.com/api/oauth/server")
                        .userAgent(User.USER_AGENT)
                        .data("grant_type", "client_credentials")
                        .data("client_id", credentials.get(0))
                        .data("client_secret", credentials.get(1))
                        .ignoreContentType(true)
                        .post();
                JSONObject result = new JSONObject(response.body().text());

                return result.get("access_token");
            }).get();

            return output;
        });
    }

    /**
     * This function is used to load the current state of a games report form. This is done using
     * futures so that the data can be loaded concurrently and not freeze the app.
     * @param cookies Contains the needed cookies for authentication
     * @param link this is a link to the page where the report form is
     * @return A Future ReportFormState containing all of the relevant information about the report form
     */
    public static Future<ReportFormState> getReportFormState(Map<String, String> cookies, String link){

        return executor.submit(() -> {
            String homeTeamScore = null;
            String awayTeamScore = null;
            String RKU = null, FBC = null, FM = null, PAS = null, COM = null, comments = null;
            List<String> maleMVPs = new ArrayList<>();
            List<String> femaleMVPs = new ArrayList<>();

            Document doc = loadWebPage(cookies, WEB_URL+link);

                Element select = doc.getElementById("game_home_score");
                for(Element option : select.children()){
                    if(option.attr("selected").equals("selected")){
                        homeTeamScore = option.text().trim();
                    }
                }
                select = doc.getElementById("game_away_score");
                for(Element option : select.children()){
                    if(option.attr("selected").equals("selected")){
                        awayTeamScore = option.text().trim();
                    }
                }

                //Check if were reporting for a home or away game
                //ids start with home/away so need to fix
                Element selectRKU = (doc.getElementById("game_home_game_report_survey_1_answer"));
                String start = "game_home_";
                if(selectRKU == null){
                    selectRKU = doc.getElementById("game_away_game_report_survey_1_answer");
                    start = "game_away_";
                }
                for(Element option : selectRKU.children()){
                    if(option.attr("selected").equals("selected")){
                        RKU = option.text().trim();
                    }
                }

                Element selectFBC =  doc.getElementById(start+"game_report_survey_2_answer");
                for(Element option : selectFBC.children()){
                    if(option.attr("selected").equals("selected")){
                        //This is done differently to fix and issue where the G in good is upper case for this Element only
                        FBC = option.text().trim().toLowerCase().replaceFirst("v", "V");
                    }
                }

                Element selectFM =  doc.getElementById(start+"game_report_survey_3_answer");
                for(Element option : selectFM.children()){
                    if(option.attr("selected").equals("selected")){
                        FM = option.text().trim();
                    }
                }

                Element selectPAS =  doc.getElementById(start+"game_report_survey_4_answer");
                for(Element option : selectPAS.children()){
                    if(option.attr("selected").equals("selected")){
                        PAS = option.text();
                    }
                }

                Element selectCOM =  doc.getElementById(start+"game_report_survey_5_answer");
                for(Element option : selectCOM.children()){
                    if(option.attr("selected").equals("selected")){
                        COM = option.text().trim();
                    }
                }

                Element commentsElem =  doc.getElementById(start+"game_report_survey_6_answer");
                comments = commentsElem.text();

                //Collect MVPS
                for(Element mvpForm : doc.getElementsByClass("form-api live spacer-half keep-popup-open person-award-form")) {
                    if (mvpForm.child(3).attr("value").equals("female_mvp")) {
                        for (Element option : mvpForm.getElementsByTag("select").last().children()) {
                            if (option.attr("selected").equals("selected")) {
                                femaleMVPs.add(option.text());
                                continue;
                            }
                        }
                    }
                    if (mvpForm.child(3).attr("value").equals("male_mvp")) {
                        for (Element option : mvpForm.getElementsByTag("select").last().children()) {
                            if (option.attr("selected").equals("selected")) {
                                maleMVPs.add(option.text());
                                continue;
                            }
                        }
                    }
                }

                return new ReportFormState.Builder()
                        .setHomeTeamScore(homeTeamScore)
                        .setAwayTeamScore(awayTeamScore)
                        .setRKU(RKU)
                        .setFBC(FBC)
                        .setFM(FM)
                        .setPAS(PAS)
                        .setCOM(COM)
                        .setComments(comments)
                        .setMaleMVPs(maleMVPs)
                        .setFemaleMVPs(femaleMVPs)
                        .setDocument(doc)
                        .build();
        });
    }

    /**
     * This function is used to submit the changes to the mvps on the report form. This is done
     * using the websites api as that is how the website does it. This is done using futures so that
     * the data can be loaded concurrently and not freeze the app.
     * @param cookies Contains the needed cookies for authentication
     * @param mvps This is A list of the new mvps to be set
     * @param OAuthToken This is the OAuth2 token needed to authenticate for the api
     * @param link This is a link to the report form
     * @return A Future Boolean indicating if the report was a success
     */
    public static Future<Boolean> reportMVPs(Map<String, String> cookies, List<String> mvps, String OAuthToken, String link) {

        return executor.submit(() -> {

                Document doc = loadWebPage(cookies, WEB_URL+link);
                int count = 0; //counts the nth mvp being reported
                String URL = "https://wds.usetopscore.com/api/person-award/edit";

            //find mvp box(s)
                for(Element mvpForm : doc.getElementsByClass("form-api live spacer-half keep-popup-open person-award-form")){
                    String playerID = null;
                    Element select = mvpForm.getElementsByTag("select").first();

                    for(Element option : select.children()){
                        if(option.text().contains(mvps.get(count))){ //get mvp id from options
                            playerID = option.attr("value");
                        }
                    }

                    Elements inputs = mvpForm.getElementsByTag("input");
                    String gameID = inputs.get(0).attr("value");
                    String teamID = inputs.get(1).attr("value");
                    String rank = inputs.get(2).attr("value");
                    String award = inputs.get(3).attr("value");

                    Map<String, String> data = new HashMap<>();
                    data.put("person_id", playerID);
                    data.put("game_id", gameID);
                    data.put("team_id", teamID);
                    data.put("rank", rank);
                    data.put("award", award);

                    if(select.child(0).attr("selected").equals("selected")){ //create award if value changed from blank to a name
                        URL = "https://wds.usetopscore.com/api/person-award/new";

                    } else if (mvps.get(count).equals("")) { //Delete award if value changed from a name to blank
                        data.put("id", inputs.get(4).attr("value"));
                        data.remove("person_id");
                        URL = "https://wds.usetopscore.com/api/person-award/delete";

                    }
                    Document reportResponse = Jsoup.connect(URL)
                            .userAgent(User.USER_AGENT)
                            .ignoreContentType(true)
                            .header("Authorization", "Bearer " + OAuthToken)
                            .data(data)
                            .post();

                    count++;
                }
            return true;
        });
    }

    /**
     * This function is used to report the scores and spirit for a given game. This is done using
     * futures so that the data can be loaded concurrently and not freeze the app.
     * @param cookies Contains the needed cookies for authentication
     * @param link This is a link to the report form
     * @param state This is the new state of the report form to be set
     * @return A Future Boolean indicating if the report was a success
     */
    public static Future<Boolean> report(Map<String, String> cookies, String link, ReportFormState state){
        return executor.submit(() -> {

                Document doc = loadWebPage(cookies, WEB_URL+link);

                Element reportForm = doc.getElementById("game-report-score-form"); //find report form

                Element gameHomeScore = doc.getElementById("game_home_score");
                setSelection(gameHomeScore, state.homeTeamScore); //set home score

                Element gameAwayScore = doc.getElementById("game_away_score");
                setSelection(gameAwayScore, state.awayTeamScore); //set away score


                Element gameSpirit1 = doc.getElementById("game_home_game_report_survey_1_answer"); //work out if reporting for home or away team
                String start = "game_home_"; //set prefix for element ids
                if(gameSpirit1 == null){
                    gameSpirit1 = doc.getElementById("game_away_game_report_survey_1_answer");
                    start = "game_away_";
                }
                setSelection(gameSpirit1, state.RKU);

                Element gameSpirit2 = doc.getElementById(start+"game_report_survey_2_answer");
                setSelection(gameSpirit2, state.FBC);

                Element gameSpirit3 = doc.getElementById(start+"game_report_survey_3_answer");
                setSelection(gameSpirit3, state.FM);

                Element gameSpirit4 = doc.getElementById(start+"game_report_survey_4_answer");
                setSelection(gameSpirit4, state.PAS);

                Element gameSpirit5 = doc.getElementById(start+"game_report_survey_5_answer");
                setSelection(gameSpirit5, state.COM);

                String commentReport = "";
                if(start.equals("game_home_")){
                    commentReport = "game[home_game_report_survey][6][answer]";
                } else {
                    commentReport = "game[away_game_report_survey][6][answer]";
                }

                FormElement form = (FormElement) reportForm;
                Connection.Response reportActionResponse = form.submit()
                        .data(commentReport, state.comments)
                        .cookies(cookies)
                        .userAgent(User.USER_AGENT)
                        .execute();
            return true;
        });
    }

    /**
     * This is a helper function for the report function. It is used to set the selected option of
     * the html select tags.
     * @param selectTag The html select tag which needs to have its option set
     * @param option the option to set the select tag to
     */
    private static void setSelection(Element selectTag, String option){
        //remove selected attribute
        Element selectedOption = selectTag.children().select("[selected]").first();
        if (selectedOption != null) {
            selectedOption.removeAttr("selected");
        }
        //set correct selected option
        for (Element op : selectTag.children()) {
            if (op.text().contains(option)) {
                op.attr("selected", "selected");
            }
        }
    }

    /**
     * This function is used to login the user on the website and collect the cookies and links to
     * relevant web pages
     * @param username the users username
     * @param password the users password
     * @return A UserLoginToken upon a successful login, null otherwise.
     */
    public static Future<UserLoginToken> loginUser(String username, String password) {
        return executor.submit(() -> {
            HashMap<String, String> cookies;
            HashMap<String, String> links = new HashMap<>();

                //log user in
                Element loginForm = loadWebPage(new HashMap<String, String>(), WEB_URL)
                        .getElementsByClass("form-vertical signin exists spacer1").first();

            Element emailField = loginForm.getElementsByClass("span3 full initial-focus span3 mailcheck").first();
                emailField.val(username);

                Element passwordField = loginForm.getElementById("signin_password");
                passwordField.val(password);

                FormElement form = (FormElement) loginForm;
            try {
                Connection.Response loginActionResponse = form.submit()
                        .userAgent(User.USER_AGENT)
                        .execute();

                cookies = (HashMap) loginActionResponse.cookies();

                Document doc = loginActionResponse.parse();
                Element userpageLink = doc.getElementsByClass("global-toolbar-user-btn ").first();

                //Collect link to upcoming schedule for later web scraping
                links.put(User.USERPAGELINK, userpageLink.attr("href"));
                links.put(User.UPCOMINGGAMESLINK, userpageLink.attr("href") + "/schedule");
                links.put(User.MISSINGRESULTSLINK, userpageLink.attr("href") + "/schedule/game_type/missing_result");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return new UserLoginToken(cookies, links);
        });
    }

    /**
     * This function is used to load the current standings of an event.
     * @param cookies Contains the needed cookies for authentication
     * @param link This is a to the event page
     * @return A list of maps containing the relevant standing information or null if none could be found.
     */
    public static Future<List<Map<String, String>>> getStandings(Map<String, String> cookies, String link){
        return executor.submit(() -> {
            ArrayList<Map<String, String>> output = new ArrayList<>();

            Document doc = loadWebPage(cookies, link+"/standings");

            Element table = doc.getElementsByClass("filter-list").first();
            Element list = table.getElementsByClass("striped-blocks spacer1").first();
            if(list.children() == null) { return null; }
            for(Element team : list.children()){
                HashMap<String, String> info = new HashMap<>();
                Element img = team.getElementsByTag("img").first();
                info.put("image", img.attr("src"));

                Element name = team.getElementsByTag("a").first();
                info.put("name", name.text());

                Element record = team.getElementsByClass("plain-link plain-link").first();
                info.put("record", record.text());

                Element spirit = team.getElementsByClass("plain-link plain-link").last();
                info.put("spirit", spirit.text());

                Element pointDiff = team.getElementsByClass("row-fluid-always").last().child(0);
                info.put("pointDiff", pointDiff.text());
                output.add(info);
            }

            return output;
        });
    }
}