package se.zinister.chronos.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import se.zinister.chronos.Adapters.RoundAdapter;
import se.zinister.chronos.Items.Game;
import se.zinister.chronos.Json.Decode;
import se.zinister.chronos.MainActivity;
import se.zinister.chronos.Match.MatchActivity;
import se.zinister.chronos.Net.Request;
import se.zinister.chronos.R;


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
        MatchActivity a = (MatchActivity)getActivity();
        if (game != null && game.oppName != null && game.oppName.equals("")) a.mTitle = getString(R.string.title_section4) + " slumpad spelare";
        else a.mTitle = getString(R.string.title_section4) + " " + game.oppName;
        a.restoreActionBar();

        if(root != null) {
            playerOnePic = (ImageView) root.findViewById(R.id.player1_photo);
            playerTwoPic = (ImageView) root.findViewById(R.id.player2_photo);
            playerOneName = (TextView) root.findViewById(R.id.player1_nameTag);
            playerTwoName = (TextView) root.findViewById(R.id.player2_nameTag);
            totalScore = (TextView) root.findViewById(R.id.totalScore);
            scoreBoard = (ListView) root.findViewById(R.id.score_list);
            FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
            game.changePicSize(150);
            if (playerOnePic != null && game.myPic != null && !game.myPic.equals("")) {
                Picasso.with(getActivity()).load(game.myPic).placeholder(R.mipmap.ic_launcher).into(playerOnePic);
            }
            if (playerTwoPic != null && game.oppPic != null && !game.oppPic.equals("")) {
                Picasso.with(getActivity()).load(game.oppPic).placeholder(R.mipmap.ic_launcher).into(playerTwoPic);
            }



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


            if (playerOneName != null) playerOneName.setText(game.myName);
            if (playerTwoName != null) playerTwoName.setText(game.oppName);
            if (totalScore != null) {
                game.calcTotalScore();
                totalScore.setText(game.myScore + " - " + game.oppScore);
            }

            if (scoreBoard != null) {
                RoundAdapter adapter = new RoundAdapter(getActivity(), R.layout.matchstatistics_item);
                adapter.addAll(game.rounds);
                scoreBoard.setAdapter(adapter);
            }

        }
    }
    public void buildGUI(View root){
        MatchActivity a = (MatchActivity)getActivity();
        if (game != null && game.oppName != null && game.oppName.equals("")) a.mTitle = getString(R.string.title_section4) + " slumpad spelare";
        else a.mTitle = getString(R.string.title_section4) + " " + game.oppName;
        a.restoreActionBar();
         playerOnePic =(ImageView) root.findViewById(R.id.player1_photo);
         playerTwoPic =(ImageView) root.findViewById(R.id.player2_photo);
         playerOneName =(TextView) root.findViewById(R.id.player1_nameTag);
         playerTwoName =(TextView) root.findViewById(R.id.player2_nameTag);
         totalScore = (TextView) root.findViewById(R.id.totalScore);
         scoreBoard = (ListView) root.findViewById(R.id.score_list);
        game.changePicSize(150);
        if (playerOnePic != null && game.myPic != null && !game.myPic.equals("")) {
            Picasso.with(getActivity()).load(game.myPic).placeholder(R.mipmap.ic_launcher).into(playerOnePic);
        }
        if (playerTwoPic != null && game.oppPic != null && !game.oppPic.equals("")) {
            Picasso.with(getActivity()).load(game.oppPic).placeholder(R.mipmap.ic_launcher).into(playerTwoPic);
        }
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
