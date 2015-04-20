package com.example.awesomeness.awesomeness.fragments;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import com.example.awesomeness.awesomeness.Adapters.StartPageAdapter;
import com.example.awesomeness.awesomeness.Json.Decode;
import com.example.awesomeness.awesomeness.MainActivity;
import com.example.awesomeness.awesomeness.Match.GamesOverview;
import com.example.awesomeness.awesomeness.Match.MatchActivity;
import com.example.awesomeness.awesomeness.Net.Request;
import com.example.awesomeness.awesomeness.R;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;



/**
 * Created by Josef on 2015-02-05.
 */
public class MainPageFragment extends BaseFragment {
    public ListView mListView;
    private MainActivity mainActivity;
    public StartPageAdapter mAdapter;
    public Decode decode = new Decode();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_question, container, false);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mListView = (ListView)rootView.findViewById(R.id.mMatches);
        if (mListView != null) {
            fab.attachToListView(mListView);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    joinGame();
                }
            });
        }
        mainActivity = (MainActivity)getActivity();
        Request r = new Request(this, mainActivity.net);
        r.execute("Login");
        return rootView;
    }

    @Override
    public int getTitleResourceId() {
        return MainActivity.MAINPAGE;
    }

    public void doneLogin(){
        Request r = new Request(this,mainActivity.net);
        r.execute("RegisterUser");
    }
    public void doneRegister(){

    }


    public void joinGame() {
        Request r = new Request(this,mainActivity.net);
        r.execute("JoinGame");
    }

    public void showMatches(String jsonString) {
        Log.d("mainActivity", " ENterd showMatches json = " + jsonString);
        Decode d = new Decode();
        ArrayList<GamesOverview> gamesOverviews = d.decodeGamesOverview(jsonString);
        if (mAdapter == null){
            mAdapter = new StartPageAdapter(getActivity(),R.layout.game_overview,gamesOverviews);
        } else {
            mAdapter.clear();
            mAdapter.addAll(gamesOverviews);
        }


        if (mListView == null)
            mListView = (ListView) getView().findViewById(R.id.mMatches);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GamesOverview g = (GamesOverview) parent.getItemAtPosition(position);
                if(g.myTurn) {
                    StartPageAdapter s = (StartPageAdapter) parent.getAdapter();
                    Intent n = new Intent(s.c, MatchActivity.class);
                    n.putExtra("gameID", g.gameID);
                    s.c.startActivity(n);
                }
            }
        });
        mListView.setAdapter(mAdapter);

    }
}
