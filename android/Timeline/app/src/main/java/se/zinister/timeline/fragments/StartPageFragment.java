package se.zinister.timeline.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import se.zinister.timeline.Adapters.DialogAdapter;
import se.zinister.timeline.Adapters.StartPageAdapter;
import se.zinister.timeline.Items.DrawerItem;
import se.zinister.timeline.Items.StartpageMessage;
import se.zinister.timeline.Json.Decode;
import se.zinister.timeline.MainActivity;
import se.zinister.timeline.Match.GamesOverview;
import se.zinister.timeline.Match.MatchActivity;
import se.zinister.timeline.Net.Request;
import se.zinister.timeline.R;





/**
 * Created by Josef on 2015-02-05.
 */
public class StartPageFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public ListView mListView;
    private MainActivity mainActivity;
    public StartPageAdapter mAdapter;
    private SwipeRefreshLayout swipeLayout;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;



    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.start_page_fragment, container, false);
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
        swipeLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.ptr_layout);
        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        swipeLayout.setOnRefreshListener(this);



        return rootView;
    }

    @Override
    public int getTitleResourceId() {
        return MainActivity.MAINPAGE;
    }
    @Override
    public void onResume() {
        super.onResume();
        doneRegister();
    }

    public void doneLogin(String returned){
        Log.d(MainActivity.TAG,"Returned login value = " + returned);
        Request r = new Request(this,mainActivity.net);
        r.execute("RegisterUser");
    }
    public void doneRegister(){
        Request r = new Request(this, mainActivity.net);
        r.execute("StartMessage");
    }


    public ArrayList <GamesOverview> sort(ArrayList<GamesOverview> list) {
        ArrayList <GamesOverview> newList = new ArrayList<>();
        boolean my = false;
        boolean opp = false;
        boolean fin = false;
        for(GamesOverview g:list) {
            if(!my && g.myTurn && g.type != 4) {
                GamesOverview section = new GamesOverview(0);
                newList.add(section);
                my = !my;
            }
            if(g.myTurn && g.type != 4) newList.add(g);
        }
        for(GamesOverview g:list) {
            if(!opp && !g.myTurn && g.type != 4) {
                GamesOverview section = new GamesOverview(2);
                newList.add(section);
                opp = !opp;
            }
            if(!g.myTurn && g.type != 4) newList.add(g);
        }
        for(GamesOverview g:list) {
            if(!fin && g.type == 4) {
                GamesOverview section = new GamesOverview(3);
                newList.add(section);
                fin = !fin;
            }
            if(g.type == 4) newList.add(g);
        }
        return newList;
    }



    public void showMatches(String jsonString) {
        Log.d("mainActivity", " ENterd showMatches json = " + jsonString);
        if (swipeLayout != null) swipeLayout.setRefreshing(false);
        Decode d = new Decode();
        StartpageMessage startpageMessage = d.decodeGamesOverview(jsonString);
        ArrayList<GamesOverview> gamesOverviews = startpageMessage.games;
        gamesOverviews = sort(gamesOverviews);
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

                StartPageAdapter s = (StartPageAdapter) parent.getAdapter();
                if (MainActivity.DEBUG) Log.d(MainActivity.TAG, "GameID in intent = " + g.gameID);
                Intent n = new Intent(s.c, MatchActivity.class);
                n.putExtra("gameID", g.gameID);
                s.c.startActivity(n);
            }

        });
        mListView.setAdapter(mAdapter);
    }

    private void showDialog(){
        final View dialogView = View.inflate(getActivity(), R.layout.new_game_dialog, null);

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);


        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // call something for API Level 11+
                    revealShow(dialogView, true, null);
                }
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
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            revealShow(dialogView, false, dialog);
                        } else {
                            dialogView.setVisibility(View.INVISIBLE);
                        }

                        break;
                    case 1:
                        Request r = new Request(dialogAdapter.caller, m.net);
                        r.execute("JoinGame");
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            revealShow(dialogView, false, dialog);
                        } else {
                            dialogView.setVisibility(View.INVISIBLE);
                        }
                        break;

                    case 2:
                        m.changeFragment(m.FIND_FRIEND);
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            revealShow(dialogView, false, dialog);
                        } else {
                            dialogView.setVisibility(View.INVISIBLE);
                        }
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





    @Override
    public void onRefresh() {
        Request r = new Request(this, mainActivity.net);
        r.execute("StartMessage");
    }


}
