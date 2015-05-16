package se.zinister.chronos.Items;

import java.util.ArrayList;

import se.zinister.chronos.Match.GamesOverview;


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
