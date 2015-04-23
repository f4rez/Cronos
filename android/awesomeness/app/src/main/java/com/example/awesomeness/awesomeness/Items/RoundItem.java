package com.example.awesomeness.awesomeness.Items;

import java.util.ArrayList;

/**
 * Created by enigma on 2015-04-21.
 */
public class RoundItem {


    public String playerOneName;
    public String playerOneID;
    public String playerOnePic;
    public int playerOneScore;
    public String playerTwoName;
    public String playerTwoID;
    public String playerTwoPic;
    public int playerTwoScore;
    public int round;
    public boolean turn;
    public ArrayList <Round> rounds;

    public RoundItem(String playerOneID, String playerOneName, String playerOnePic, String playerTwoID, String playerTwoName, String playerTwoPic, int round, boolean turn) {
        this.playerOneID = playerOneID;
        this.playerOneName = playerOneName;
        this.playerOnePic = playerOnePic;
        this.playerTwoID = playerTwoID;
        this.playerTwoName = playerTwoName;
        this.playerTwoPic = playerTwoPic;
        this.round = round;
        this.turn = turn;
    }


    public void calcTotalScore() {
        for (Round round :rounds){
            playerOneScore += round.playerOneScore;
            playerTwoScore += round.playerTwoScore;
        }
    }

    public void addRound(Round r){
        rounds.add(r);
    }



    public static class Round {
        public int playerOneScore;
        public int playerTwoScore;
        public Round () {

        }
    }


}
