package se.zinister.timeline.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import se.zinister.timeline.Adapters.RoundAdapter;
import se.zinister.timeline.Items.Game;
import se.zinister.timeline.Json.Decode;
import se.zinister.timeline.MainActivity;
import se.zinister.timeline.Match.MatchActivity;
import se.zinister.timeline.Net.Request;
import se.zinister.timeline.R;


/**
 * Created by enigma on 2015-04-21.
 */
public class MatchStatistics extends BaseFragment {
    private Game game;
    public Decode d= new Decode();
    ImageView playerOnePic;
    ImageView playerTwoPic;
    TextView playerOneName;
    TextView playerTwoName ;
    TextView totalScore;
    ListView scoreBoard;
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


         playerOnePic =(ImageView) root.findViewById(R.id.player1_photo);
         playerTwoPic =(ImageView) root.findViewById(R.id.player2_photo);
         playerOneName =(TextView) root.findViewById(R.id.player1_nameTag);
         playerTwoName =(TextView) root.findViewById(R.id.player2_nameTag);
         totalScore = (TextView) root.findViewById(R.id.totalScore);
         scoreBoard = (ListView) root.findViewById(R.id.score_list);
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

         playerOnePic =(ImageView) root.findViewById(R.id.player1_photo);
         playerTwoPic =(ImageView) root.findViewById(R.id.player2_photo);
         playerOneName =(TextView) root.findViewById(R.id.player1_nameTag);
         playerTwoName =(TextView) root.findViewById(R.id.player2_nameTag);
         totalScore = (TextView) root.findViewById(R.id.totalScore);
         scoreBoard = (ListView) root.findViewById(R.id.score_list);
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
            fab.setVisibility(View.INVISIBLE);
        }
        if (playerOneName != null) {

            playerOneName.setText(game.myName);

        }
        if (playerTwoName != null) {

            playerTwoName.setText(game.oppName);

        }
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

    public void saveGame(String in){
        game = d.decodeGame(in);
        MatchActivity m = (MatchActivity) getActivity();
        if (m != null) {
            m.game = game;
            buildGUI();
        }
    }


}
