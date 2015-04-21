package com.example.awesomeness.awesomeness.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.example.awesomeness.awesomeness.Adapters.DialogAdapter;
import com.example.awesomeness.awesomeness.Adapters.DrawerAdapter;
import com.example.awesomeness.awesomeness.Adapters.StartPageAdapter;
import com.example.awesomeness.awesomeness.Items.DrawerItem;
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
                    showRateDialog(getActivity(), getActivity().getSharedPreferences("", 0).edit());
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
    public void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Betygsätt " );


        dialog.setContentView(R.layout.new_game_dialog);
        ListView newGameListView = (ListView) dialog.findViewById(R.id.newGameListView);
        DrawerItem d1 = new DrawerItem("", "Utmana en vän",R.drawable.ic_action_new);
        DrawerItem d2 = new DrawerItem("","Slumpad motståndare",R.drawable.ic_action_overflow);
        DrawerItem d3 = new DrawerItem("","Sök spelare",R.drawable.ic_launcher);
        DialogAdapter adapter = new DialogAdapter(this,R.layout.new_game_listview_item);
        adapter.add(d1);
        adapter.add(d2);
        adapter.add(d3);
        newGameListView.setAdapter(adapter);
        newGameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogAdapter dialogAdapter = (DialogAdapter) parent.getAdapter();
                MainActivity m = (MainActivity)dialogAdapter.caller.getActivity();
                switch (position) {
                    case 0:
                        m.changeFragment(m.CHALLENGE_FRIEND);
                        break;
                    case 1:
                        Request r = new Request(dialogAdapter.caller, m.net);
                        r.execute("JoinGame");
                        break;

                    case 2:
                        m.changeFragment(m.FIND_FRIEND);
                        break;
                }
            }
        });
        dialog.show();
    }

}
