package com.example.firstapp.model;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Team {

    private String name, imageUrl;
    private List<String> maleMatchups, femaleMatchups;

    public Team(String name, String imageUrl, List<String> maleMatchups, List<String> femaleMatchups) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.maleMatchups = maleMatchups;
        this.femaleMatchups = femaleMatchups;
    }

    public static List<Future<List<Team>>> loadTeams(String eventTeamUrl, Map<String, String> cookies){
        ExecutorService executor = Executors.newCachedThreadPool();
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        final String WEB_URL = "https://wds.usetopscore.com";

        List<Future<List<Team>>> futures = new ArrayList<>();

        //Load the first 5 pages of teams (THIS SHOUlD BE ALL OF THEM (MAX 70TEAMS LOL))
        for(int i = 1; i < 6; i++){
            futures.add(executor.submit(new loadTeamsTask(WEB_URL + eventTeamUrl + "?page=" + i, cookies)));
        }

        return futures;
    }

    private static class loadTeamsTask implements Callable<List<Team>> {

        private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        private String URL;
        private Map<String, String> cookies;

        public loadTeamsTask(String URL, Map<String, String> cookies){
            System.out.println(URL);
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
            for(Element teamDiv : doc.getElementsByClass("span4 media-item-wrapper spacer1 ")){
                String teamName, teamImage;

                teamName = teamDiv.getElementsByTag("h3").first().text();
                teamImage = teamDiv.getElementsByClass("media-item-tile media-item-tile-normal media-item-tile-cover").first().attr("style").substring(23);

                System.out.println("TEAM: "+teamName+" With IMAGE: "+teamImage);
            }
            return null;
        }
    }
}
