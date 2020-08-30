package com.example.firstapp.model;

import android.annotation.SuppressLint;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Team implements Serializable {

    private String name, imageUrl;
    private List<String> maleMatchups, femaleMatchups;

    public Team(String name, String imageUrl, List<String> maleMatchups, List<String> femaleMatchups) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.maleMatchups = maleMatchups;
        this.femaleMatchups = femaleMatchups;
    }

    public static Future<List<Team>> loadTeams(String eventTeamUrl, Map<String, String> cookies){
        ExecutorService executor = Executors.newCachedThreadPool();
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        final String WEB_URL = "https://wds.usetopscore.com";

        List<Future<List<Team>>> futures = new ArrayList<>();
        return executor.submit(() -> {
            List<Team> teams = new ArrayList<>();
            for(int i = 1; i < 6; i++){
                futures.add(executor.submit(new loadTeamsTask(WEB_URL + eventTeamUrl + "/teams?page=" + i, cookies)));
            }

            for(Future f : futures){
                teams.addAll((Collection<? extends Team>) f.get());
            }

            return teams;
        });
        //Load the first 5 pages of teams (THIS SHOUlD BE ALL OF THEM (MAX 70TEAMS LOL))


    }

    public String getName() {
        return this.name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getMaleMatchups(){
        return this.maleMatchups;
    }
    public List<String> getFemaleMatchups(){
        return this.femaleMatchups;
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
