package com.example.wuapp;

import com.example.wuapp.parsers.WDSParser;
import com.example.wuapp.model.Game;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Set;

public class WDSParserTest {

    Document missing_results_page;
    Document with_results_page;

    private Document loadUpcomingGamesTestFile(){

        BufferedInputStream in = (BufferedInputStream) this.getClass().getClassLoader().getResourceAsStream("upcoming.html");
        String strFileContents = "";
        try {
            byte[] contents = new byte[1024];

            int bytesRead = 0;

            while ((bytesRead = in.read(contents)) != -1) {
                strFileContents += new String(contents, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Jsoup.parse(strFileContents);
    }

    private Document loadMissingResultTestFile(){

        BufferedInputStream in = (BufferedInputStream) this.getClass().getClassLoader().getResourceAsStream("missing_results.html");
        String strFileContents = "";
        try {
            byte[] contents = new byte[1024];

            int bytesRead = 0;

            while ((bytesRead = in.read(contents)) != -1) {
                strFileContents += new String(contents, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Jsoup.parse(strFileContents);
    }

    private Document loadWithResultTestFile(){

        BufferedInputStream in = (BufferedInputStream) this.getClass().getClassLoader().getResourceAsStream("with_results.html");
        String strFileContents = "";
        try {
            byte[] contents = new byte[1024];

            int bytesRead = 0;

            while ((bytesRead = in.read(contents)) != -1) {
                strFileContents += new String(contents, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Jsoup.parse(strFileContents);
    }

    @Test
    public void test_upcoming_games_parsing_results() {
        WDSParser parser = new WDSParser();
        Document games_page =  loadUpcomingGamesTestFile();

        assert games_page != null;
        Set<Game> games = WDSParser.parseGames(games_page);

        assert games != null;
        assert !games.isEmpty();
    }

    @Test
    public void test_missing_results_games_parsing_results() {
        WDSParser parser = new WDSParser();
        Document games_page =  loadMissingResultTestFile();

        assert games_page != null;
        Set<Game> games = WDSParser.parseGames(games_page);

        assert games != null;
        assert !games.isEmpty();
    }

    @Test
    public void test_with_results_games_parsing_results() {
        WDSParser parser = new WDSParser();
        Document games_page =  loadWithResultTestFile();

        assert games_page != null;
        Set<Game> games = WDSParser.parseGames(games_page);

        assert games != null;
        assert !games.isEmpty();
    }

}
