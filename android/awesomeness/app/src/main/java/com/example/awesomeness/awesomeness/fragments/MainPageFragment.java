package com.example.awesomeness.awesomeness.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
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
                    showDialog();
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
        Request r = new Request(this,mainActivity.net);
        r.execute("StartMess");
    }

    public void showMatches(String jsonString) {
        Log.d("mainActivity", " ENterd showMatches json = " + jsonString);
        Decode d = new Decode();
        ArrayList<GamesOverview> gamesOverviews = d.decodeGamesOverview(jsonString);
        if (mAdapter == null) {
            mAdapter = new StartPageAdapter(getActivity(), R.layout.game_overview, gamesOverviews);
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
                if (g.myTurn) {
                    StartPageAdapter s = (StartPageAdapter) parent.getAdapter();
                    Intent n = new Intent(s.c, MatchActivity.class);
                    n.putExtra("gameID", g.gameID);
                    s.c.startActivity(n);
                }
            }
        });
        mListView.setAdapter(mAdapter);
    }

    private void showDialog(){
        final View dialogView = View.inflate(getActivity(), R.layout.new_game_dialog, null);

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(false);


        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                revealShow(dialogView, true, null);
            }
        });
        ListView newGameListView = (ListView) dialogView.findViewById(R.id.newGameListView);
        DrawerItem d1 = new DrawerItem("","Utmana en vän",R.drawable.ic_action_person);
        DrawerItem d2 = new DrawerItem("","Slumpad motståndare",R.drawable.ic_action_group);
        DrawerItem d3 = new DrawerItem("","Sök spelare",R.drawable.ic_action_search);
        DialogAdapter adapter = new DialogAdapter(this,R.layout.new_game_listview_item);
        adapter.add(d1);
        adapter.add(d2);
        adapter.add(d3);
        newGameListView.setAdapter(adapter);
        newGameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogAdapter dialogAdapter = (DialogAdapter) parent.getAdapter();
                MainActivity m = (MainActivity) dialogAdapter.caller.getActivity();
                switch (position) {
                    case 0:
                        m.changeFragment(m.CHALLENGE_FRIEND);
                        revealShow(dialogView, false, dialog);
                        break;
                    case 1:
                        Request r = new Request(dialogAdapter.caller, m.net);
                        r.execute("JoinGame");
                        revealShow(dialogView, false, dialog);
                        break;

                    case 2:
                        m.changeFragment(m.FIND_FRIEND);
                        revealShow(dialogView, false, dialog);
                        break;
                }
            }
        });



        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

    }

    private void revealShow(View rootView, boolean reveal, final AlertDialog dialog){
        final View view = rootView.findViewById(R.id.reveal_view);
        int w = view.getWidth();
        int h = view.getHeight();
        float maxRadius = (float) Math.sqrt(w * w / 4 + h * h / 4);

        if(reveal){
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view,
                    w / 2, h / 2, 0, maxRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.start();

        } else {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, w / 2, h / 2, maxRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });

            anim.start();
        }

    }





}
