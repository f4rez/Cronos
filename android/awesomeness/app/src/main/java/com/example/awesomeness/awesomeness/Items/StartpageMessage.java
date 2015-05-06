package com.example.awesomeness.awesomeness.Items;

import com.example.awesomeness.awesomeness.Match.GamesOverview;

import java.util.ArrayList;

/**
 * Created by josef on 2015-05-05.
 */
public class StartpageMessage {
    public ArrayList<GamesOverview> games;
    public ArrayList<Challenge> challenges;


    public StartpageMessage() {
        games = new ArrayList<>();
        challenges = new ArrayList<>();
    }
}
