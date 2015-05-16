package se.zinister.chronos.Match;

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
    public String opponentPic;
    public boolean myTurn;
    public int type;


    public GamesOverview(int type) {
        this.type = type;
    }


    public GamesOverview(int g, int m, int o, int n, String oName,String oID, String oPic, boolean turn) {
        gameID = g;
        myPoint = m;
        opponentPoint= o;
        numberOfTurns = n;
        opponentName = oName;
        opponentID = oID;
        myTurn = turn;
        opponentPic = oPic;
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

    public void changePicSize(int size) {
        if(opponentPic != null && !opponentPic.equals(""))
        opponentPic = opponentPic.substring(0, opponentPic.indexOf("sz=")) + "sz=" + size;

    }

}
