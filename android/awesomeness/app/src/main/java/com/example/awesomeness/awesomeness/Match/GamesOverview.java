package com.example.awesomeness.awesomeness.Match;

import android.content.ClipData;

/**
 * Created by josef on 2015-04-14.
 */
public class GamesOverview {

    public Integer gameID;
    public int myPoint;
    public int opponentPoint;
    public int numberOfTurns;
    public String opponentName;
    public String opponentID;
    public boolean myTurn;
    public int type;


    public GamesOverview(int type) {
        this.type = type;
    }


    public GamesOverview(int g, int m, int o, int n, String oName,String oID, boolean turn) {
        gameID = g;
        myPoint = m;
        opponentPoint= o;
        numberOfTurns = n;
        opponentName = oName;
        opponentID = oID;
        myTurn = turn;
        type = 1;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GamesOverview))
            return false;
        if (obj == this)
            return true;
        GamesOverview g = (GamesOverview) obj;
        return gameID == g.gameID;

    }

}
