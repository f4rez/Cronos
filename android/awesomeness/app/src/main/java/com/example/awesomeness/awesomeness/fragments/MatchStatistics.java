package com.example.awesomeness.awesomeness.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.awesomeness.awesomeness.Adapters.RoundAdapter;
import com.example.awesomeness.awesomeness.Items.Game;
import com.example.awesomeness.awesomeness.Json.Decode;
import com.example.awesomeness.awesomeness.MainActivity;
import com.example.awesomeness.awesomeness.Match.MatchActivity;
import com.example.awesomeness.awesomeness.Net.Request;
import com.example.awesomeness.awesomeness.R;
import com.melnykov.fab.FloatingActionButton;

/**
 * Created by enigma on 2015-04-21.
 */
public class MatchStatistics extends BaseFragment {
    private Game game;
    public Decode d= new Decode();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.match_statistics, container, false);
        MatchActivity m = (MatchActivity) getActivity();
        if (m.game == null) {
            if (MainActivity.DEBUG) Log.d(MainActivity.TAG, "game == null");
            getGame();
        } else {
            game = m.game;
            buildGUI(rootView);
        }
        return rootView;
    }

    @Override
    public int getTitleResourceId() {
        return MatchActivity.STATISTICS;
    }


    public void getGame(){
        MatchActivity m = (MatchActivity) getActivity();
        Request r = new Request(this, m.net);
        r.execute("GetGame", String.valueOf(m.gameID));
    }


    public void buildGUI(){
        View root = getView();


        ImageView playerOnePic =(ImageView) root.findViewById(R.id.player1_photo);
        ImageView playerTwoPic =(ImageView) root.findViewById(R.id.player2_photo);
        TextView playerOneName =(TextView) root.findViewById(R.id.player1_nameTag);
        TextView playerTwoName =(TextView) root.findViewById(R.id.player2_nameTag);
        TextView totalScore = (TextView) root.findViewById(R.id.totalScore);
        ListView scoreBoard = (ListView) root.findViewById(R.id.score_list);
        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        if (game.turn) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MatchActivity m = (MatchActivity) getActivity();
                    m.changeFragment(MatchActivity.MATCHPAGE);
                }
            });
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           getActivity().onBackPressed();
                                       }
        });
            fab.setImageResource(R.drawable.ic_action_overflow);
        }


        if (playerOneName != null) playerOneName.setText(game.myName);
        if (playerTwoName != null) playerTwoName.setText(game.oppName);
        if (totalScore != null) {
            game.calcTotalScore();
            totalScore.setText(game.myScore + " - " + game.oppScore);
        }

        if (scoreBoard != null) {
            RoundAdapter adapter = new RoundAdapter(getActivity(),R.layout.matchstatistics_item);
            adapter.addAll(game.rounds);
            scoreBoard.setAdapter(adapter);
        }


    }
    public void buildGUI(View root){
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);
        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeIn);
        ImageView playerOnePic =(ImageView) root.findViewById(R.id.player1_photo);
        ImageView playerTwoPic =(ImageView) root.findViewById(R.id.player2_photo);
        TextView playerOneName =(TextView) root.findViewById(R.id.player1_nameTag);
        TextView playerTwoName =(TextView) root.findViewById(R.id.player2_nameTag);
        TextView totalScore = (TextView) root.findViewById(R.id.totalScore);
        ListView scoreBoard = (ListView) root.findViewById(R.id.score_list);
        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        if (game.turn) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MatchActivity m = (MatchActivity) getActivity();
                    m.changeFragment(MatchActivity.MATCHPAGE);
                }
            });
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
            fab.setImageResource(R.drawable.ic_action_overflow);
        }
        if (playerOneName != null) {
            playerOneName.setText(game.myName);
            playerOneName.setAnimation(animation);
            playerOneName.startAnimation(animation);
        }
        if (playerTwoName != null) {
            playerTwoName.setText(game.oppName);
            playerTwoName.setAnimation(animation);
            playerTwoName.startAnimation(animation);
        }
        if (totalScore != null) {
            game.calcTotalScore();
            totalScore.setText(game.myScore + " - " + game.oppScore);
            totalScore.setAnimation(animation);
            totalScore.startAnimation(animation);
        }


        if (scoreBoard != null) {
            RoundAdapter adapter = new RoundAdapter(getActivity(),R.layout.matchstatistics_item);
            adapter.addAll(game.rounds);
            scoreBoard.setAdapter(adapter);
            scoreBoard.setAnimation(animation);
            scoreBoard.startAnimation(animation);
        }


    }

    public void saveGame(String in){
        game = d.decodeGame(in);
        MatchActivity m = (MatchActivity) getActivity();
        if (m != null) {
            m.game = game;
            buildGUI();
        }
    }
}
