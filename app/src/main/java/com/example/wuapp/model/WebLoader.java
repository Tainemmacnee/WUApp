package com.example.wuapp.model;

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
     *
     * @param cookies Contains the needed cookies for authentication
     * @param link    This is the link the the web page to the loaded and parsed
     * @return the web page parsed as A JSoup Document
     */
    public static Document loadWebPage(Map<String, String> cookies, String link) {
        Document result = null;
        long t1 = System.currentTimeMillis();
        try {
            Connection.Response loadPageResponse = Jsoup.connect(link)
                    .method(Connection.Method.GET)
                    .userAgent(User.USER_AGENT)
                    .cookies(cookies)
                    .execute();
            System.out.println("Time to load " + link + ": " + (System.currentTimeMillis() - t1));
            result = loadPageResponse.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * This function is used to scrape and parse all of the games from the users schedule page.
     *
     * @param cookies Contains the needed cookies for authentication
     * @param link    A link to the users schedule page
     * @return A Future list of Game objects collected from the web page
     */
    public static Future<List<Game>> LoadGames(Map<String, String> cookies, String link) {

        return executor.submit(() -> {
            List<Game> output = new ArrayList<>();
            Document doc = loadWebPage(cookies, WEB_URL + link);

            Elements gameBlocks = doc.getElementsByClass("striped-block");
            for (Element gameBlock : gameBlocks) {
                String homeTeamName = null, homeTeamImg = null, awayTeamName = null, awayTeamImg = null,
                        Event = null, date = null, time = null, location = null, homeTeamScore = "?",
                        homeTeamSpirit = "?", awayTeamScore = "?", awayTeamSpirit = "?", reportLink = null;

                //find divs containing team info
                Element homeTeamElem = gameBlock.getElementsByClass("game-participant").first();
                Element awayTeamElem = gameBlock.getElementsByClass("game-participant").last();

                Element scoreBoxDiv = gameBlock.getElementsByClass("schedule-score-box").first();
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
                Element datetimeElem = gameBlock.getElementsByClass("clearfix").first();
                String[] datetime = datetimeElem.text().trim().split(" ");

                date = datetime[0] + " " + datetime[1];
                time = datetime[2] + " " + datetime[3];

                //Location and Event
                Element locationLeagueElem = gameBlock.getElementsByClass("clearfix").last();
                Element locationElem = locationLeagueElem.getElementsByClass("push-left").first();
                location = locationElem.text().trim();

                Element LeagueElem = locationLeagueElem.getElementsByClass("push-right").first();
                Event = LeagueElem.text().trim();

                output.add(new Game.Builder()
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

            return output;
        });
    }

    /**
     * This function is used to scrape all of the users events from the home page. This is done
     * using futures so that the data can be loaded concurrently and not freeze the app.
     *
     * @param cookies Contains the needed cookies for authentication
     * @return A Future list of Event objects collected from the web page
     */
    public static Future<List<Event>> LoadEvents(Map<String, String> cookies) {
        List<Event> output = new ArrayList<>();
        long t1 = System.currentTimeMillis();
        return executor.submit(() -> {
            Document doc = loadWebPage(cookies, WEB_URL);

            String eventName, eventImg, standingsLink;
            Future<List<Team>> eventTeams;

            Elements eventLinks = doc.getElementsByClass("global-toolbar-subnav-img-item plain-link");
            for (Element e : eventLinks) {
                if (e.attr("href").startsWith("/e/")) {
                    eventTeams = WebLoader.loadTeams(e.attr("href"), cookies);
                    standingsLink = WEB_URL + e.attr("href");
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
     *
     * @param eventTeamUrl This is the link to the event page
     * @param cookies      Contains the needed cookies for authentication
     * @return A Future list of Team objects collected from the first 5 pages of teams
     */
    public static Future<List<Team>> loadTeams(String eventTeamUrl, Map<String, String> cookies) {
        List<Future<List<Team>>> futures = new ArrayList<>();

        return executor.submit(() -> {
            List<Team> teams = new ArrayList<>();
            for (int i = 1; i < 6; i++) {
                futures.add(executor.submit(new WebLoader.loadTeamsTask(WEB_URL + eventTeamUrl + "/teams?page=" + i, cookies)));
            }

            for (Future f : futures) {
                teams.addAll((Collection<? extends Team>) f.get());
            }

            return teams;
        });
        //Load the first 5 pages of teams (THIS SHOUlD BE ALL OF THEM (MAX 70TEAMS))
    }

    /**
     * This class is a helper class for loadTeams that is used to load team objects from a web page
     */
    private static class loadTeamsTask implements Callable<List<Team>> {

        private String URL;
        private Map<String, String> cookies;

        public loadTeamsTask(String URL, Map<String, String> cookies) {
            this.URL = URL;
            this.cookies = cookies;
        }

        /**
         * This function is used to execute the task of loading the teams from URL
         *
         * @return a List of Team objects taken from the web page
         */
        @Override
        public List<Team> call() {
            Document doc = loadWebPage(cookies, URL);

            List<Team> teams = new ArrayList<>();
            for (Element teamDiv : doc.getElementsByClass("span4 media-item-wrapper spacer1 ")) {
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
    }

    /**
     * This function is used to load some important user related information e.g. Name,
     * profile image, ect. This is done using futures so that the data can be loaded concurrently
     * and not freeze the app.
     *
     * @param loginToken This is the token created when the user logs in
     * @return A Future User object
     */
    public static Future<User> loadUser(UserLoginToken loginToken) {
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
            for (int i = 0; i < profileInfoElem.children().size(); i++) {
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
     *
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

                for (Element row : table.getElementsByTag("tr")) { //find id and secret
                    output.add(row.getElementsByTag("td").first().text());
                }

                output.remove(2); //remove useless string

                return output;
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
     *
     * @param cookies Contains the needed cookies for authentication
     * @param link    this is a link to the page where the report form is
     * @return A Future ReportFormState containing all of the relevant information about the report form
     */
    public static Future<ReportFormState> getReportFormState(Map<String, String> cookies, String link) {

        return executor.submit(() -> {
            Document doc = loadWebPage(cookies, WEB_URL + link);
            return parseReportFormPage(doc);
        });
    }

    /**
     * This function is used to parse a report form page into its respective ReportFormState object
     *
     * @param doc This is a JSoup Document representing a report form webpage
     * @return A ReportFormState Object representing the given Document/page
     */
    public static ReportFormState parseReportFormPage(Document doc) {
        String comments = null;
        int homeTeamScore, awayTeamScore, RKU, FBC, FM, PAS, COM;
        List<String> maleMVPs = new ArrayList<>();
        List<String> femaleMVPs = new ArrayList<>();

        Element select = doc.getElementById("game_home_score");
        homeTeamScore = getSelectedIndex(select);

        select = doc.getElementById("game_away_score");
        awayTeamScore = getSelectedIndex(select);

        //Check if were reporting for a home or away game
        //ids start with home/away so need to find prefix
        Element selectRKU = (doc.getElementById("game_home_game_report_survey_1_answer"));
        String start = "game_home_";
        if (selectRKU == null) {
            selectRKU = doc.getElementById("game_away_game_report_survey_1_answer");
            start = "game_away_";
        }
        RKU = getSelectedIndex(selectRKU);

        Element selectFBC = doc.getElementById(start + "game_report_survey_2_answer");
        FBC = getSelectedIndex(selectFBC);

        Element selectFM = doc.getElementById(start + "game_report_survey_3_answer");
        FM = getSelectedIndex(selectFM);

        Element selectPAS = doc.getElementById(start + "game_report_survey_4_answer");
        PAS = getSelectedIndex(selectPAS);

        Element selectCOM = doc.getElementById(start + "game_report_survey_5_answer");
        COM = getSelectedIndex(selectCOM);

        Element commentsElem = doc.getElementById(start + "game_report_survey_6_answer");
        comments = commentsElem.text();

        //Collect MVPS
        for (Element mvpForm : doc.getElementsByClass("form-api live spacer-half keep-popup-open person-award-form")) {
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
    }

    /**
     * This function is used to report the scores, mvps and spirit for a given game. This is done using
     * futures so that the data can be loaded concurrently and not freeze the app.
     *
     * @param cookies   Contains the needed cookies for authentication when submitting
     * @param formState This is the new state of the report form to be set
     * @return A Future Boolean indicating if the report was a success
     */
    public static Future<Boolean> submitFormState(Map<String, String> cookies, ReportFormState formState) {
        return executor.submit(() -> {
            Document doc = formState.doc;

            Element reportForm = doc.getElementById("game-report-score-form"); //find report form

            Element gameHomeScore = doc.getElementById("game_home_score");
            setSelectedIndex(gameHomeScore, formState.homeTeamScore); //set home score

            Element gameAwayScore = doc.getElementById("game_away_score");
            setSelectedIndex(gameAwayScore, formState.awayTeamScore); //set away score


            Element gameSpirit1 = doc.getElementById("game_home_game_report_survey_1_answer"); //work out if reporting for home or away team
            String start = "game_home_"; //set prefix for element ids
            if (gameSpirit1 == null) {
                gameSpirit1 = doc.getElementById("game_away_game_report_survey_1_answer");
                start = "game_away_";
            }
            setSelectedIndex(gameSpirit1, formState.RKU);

            Element gameSpirit2 = doc.getElementById(start + "game_report_survey_2_answer");
            setSelectedIndex(gameSpirit2, formState.FBC);

            Element gameSpirit3 = doc.getElementById(start + "game_report_survey_3_answer");
            setSelectedIndex(gameSpirit3, formState.FM);

            Element gameSpirit4 = doc.getElementById(start + "game_report_survey_4_answer");
            setSelectedIndex(gameSpirit4, formState.PAS);

            Element gameSpirit5 = doc.getElementById(start + "game_report_survey_5_answer");
            setSelectedIndex(gameSpirit5, formState.COM);

            String commentReport;
            if (start.equals("game_home_")) {
                commentReport = "game[home_game_report_survey][6][answer]";
            } else {
                commentReport = "game[away_game_report_survey][6][answer]";
            }

            FormElement form = (FormElement) reportForm;
            Connection.Response reportActionResponse = form.submit()
                    .data(commentReport, formState.comments)
                    .cookies(cookies)
                    .userAgent(User.USER_AGENT)
                    .execute();

            return true;
        });
    }

    public static Future<Boolean> reportMVP(Map<String, String> data, String OAuthToken, String link) {
        return executor.submit(() -> {

            Document reportResponse = Jsoup.connect(link)
                    .userAgent(User.USER_AGENT)
                    .ignoreContentType(true)
                    .header("Authorization", "Bearer " + OAuthToken)
                    .data(data)
                    .post();

            return true;
        });
    }

    /**
     * This function finds the id of a player from an html select tag where the players name is an option
     *
     * @param selectTag The encapsulating select tag
     * @param MVPName   The name of the player who id were are trying to get
     * @return The id of the player or null if they weren't found
     */
    public static String getMVPId(Element selectTag, String MVPName) {
        for (Element option : selectTag.children()) {
            if (option.text().contains(MVPName)) { //get mvp id from options
                return option.attr("value");
            }
        }
        return null;
    }

    /**
     * This is a helper function for the parseReportForm function. It is used to get the selected
     * option from an html select tag
     *
     * @param selectTag The html select tag to find the selected index of
     * @return The index of the selected option or 0 if none are selected
     */
    public static int getSelectedIndex(Element selectTag) {
        for (int i = 0; i < selectTag.childrenSize(); i++) {
            if (selectTag.child(i).attr("selected").equals("selected")) {
                return i;
            }
        }
        return 0;
    }

    /**
     * This is a helper function for the report function. It is used to set the selected option of
     * the html select tags.
     *
     * @param selectTag The html select tag which needs to have its option set
     * @param index     the index of the option to set the select tag to
     */
    private static void setSelectedIndex(Element selectTag, int index) {
        //remove selected attribute
        Element selectedOption = selectTag.children().select("[selected]").first();
        if (selectedOption != null) {
            selectedOption.removeAttr("selected");
        }
        //set correct selected option
        selectTag.child(index).attr("selected", "selected");
    }

    /**
     * This function is used to login the user on the website and collect the cookies and links to
     * relevant web pages
     *
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
                links.put(User.GAMESLINK, userpageLink.attr("href") + "/schedule/event_id/active_events_only/game_type/all");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return new UserLoginToken(cookies, links);
        });
    }

    /**
     * This function is used to load the current standings of an event.
     *
     * @param cookies Contains the needed cookies for authentication
     * @param link    This is a to the event page
     * @return A list of maps containing the relevant standing information or null if none could be found.
     */
    public static Future<List<Map<String, String>>> getStandings(Map<String, String> cookies, String link) {
        return executor.submit(() -> {
            ArrayList<Map<String, String>> output = new ArrayList<>();

            Document doc = loadWebPage(cookies, link + "/standings");

            Element table = doc.getElementsByClass("filter-list").first();
            Element list = table.getElementsByClass("striped-blocks spacer1").first();
            if (list.children() == null) {
                return null;
            }
            for (Element team : list.children()) {
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