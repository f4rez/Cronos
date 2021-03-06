package se.zinister.chronos.Items;

import android.util.Log;

import java.util.ArrayList;

import se.zinister.chronos.MainActivity;


/**
 * Created by enigma on 2015-04-21.
 */
public class Game {


    public String myName;
    public String myId;
    public String myPic;
    public int myScore;
    public String oppName;
    public String oppId;
    public String oppPic;
    public int oppScore;
    public int round;
    public boolean turn;
    public ArrayList <Round> rounds = new ArrayList<>();

    public Game(String playerOneID, String playerOneName, String playerOnePic,
                String playerTwoID, String playerTwoName, String playerTwoPic, int round, boolean turn) {
        String name = MainActivity.MY_NAME;
        if(name != null) {
            if (name.equals(playerOneName)) {
                if (MainActivity.DEBUG)
                    Log.d(MainActivity.TAG, "Firstplayer" + name + " - " + playerOneName);
                this.myId = playerOneID;
                this.myName = playerOneName;
                this.myPic = playerOnePic;
                this.oppId = playerTwoID;
                this.oppName = playerTwoName;
                this.oppPic = playerTwoPic;
                this.turn = !turn;
            } else {
                if (MainActivity.DEBUG)
                    Log.d(MainActivity.TAG, "Secondplayer " + name + " - " + playerOneName);
                this.myId = playerTwoID;
                this.myName = playerTwoName;
                this.myPic = playerTwoPic;
                this.oppId = playerOneID;
                this.oppName = playerOneName;
                this.oppPic = playerOnePic;
                this.turn = turn;
            }
            this.round = round;
        }
    }


    public void calcTotalScore() {
        for (Round round :rounds){
            if(round.myRoundScore > -1)
            myScore += round.myRoundScore;
            if(round.oppRoundScore > -1)
            oppScore += round.oppRoundScore;
        }
    }

    public void addRound(Round r){
        rounds.add(r);
    }



    public void changePicSize(int size) {
        if(oppPic != null && !oppPic.equals(""))
        oppPic = oppPic.substring(0, oppPic.indexOf("sz=")) + "sz=" + size;
        if(myPic != null && !myPic.equals(""))
        myPic = myPic.substring(0, myPic.indexOf("sz=")) + "sz=" + size;
    }





    public static class Round {
        public int myRoundScore;
        public int oppRoundScore;
        public Round () {
            myRoundScore = -1;
            oppRoundScore = -1;

        }
    }


}
