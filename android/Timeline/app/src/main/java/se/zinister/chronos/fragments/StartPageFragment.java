package se.zinister.chronos.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import se.zinister.chronos.Adapters.DialogAdapter;
import se.zinister.chronos.Adapters.StartPageAdapter;
import se.zinister.chronos.Items.Challenge;
import se.zinister.chronos.Items.DrawerItem;
import se.zinister.chronos.Items.StartpageMessage;
import se.zinister.chronos.Json.Decode;
import se.zinister.chronos.MainActivity;
import se.zinister.chronos.Match.GamesOverview;
import se.zinister.chronos.Match.MatchActivity;
import se.zinister.chronos.Net.Request;
import se.zinister.chronos.R;





/**
 * Created by Josef on 2015-02-05.
 *
 */
public class StartPageFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public ListView mListView;
    private MainActivity mainActivity;
    public StartPageAdapter mAdapter;
    private SwipeRefreshLayout swipeLayout;
    private StartPageFragment s = this;

    private ArrayList<Challenge> challenges;

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
                    showFabDialog();
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
        Log.d(MainActivity.TAG, "Returned login value = " + returned);
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
                my = true;
            }
            if(g.myTurn && g.type != 4) newList.add(g);
        }
        for(GamesOverview g:list) {
            if(!opp && !g.myTurn && g.type != 4) {
                GamesOverview section = new GamesOverview(2);
                newList.add(section);
                opp = true;
            }
            if(!g.myTurn && g.type != 4) newList.add(g);
        }
        for(GamesOverview g:list) {
            if(!fin && g.type == 4) {
                GamesOverview section = new GamesOverview(3);
                newList.add(section);
                fin = true;
            }
            if(g.type == 4) newList.add(g);
        }
        return newList;
    }



    public void showMatches(String jsonString) {

        if (swipeLayout != null) swipeLayout.setRefreshing(false);
        Decode d = new Decode();
        StartpageMessage startpageMessage = d.decodeGamesOverview(jsonString);
        ArrayList<GamesOverview> gamesOverviews = startpageMessage.games;
        if(gamesOverviews.size() > 0) {
            challenges = startpageMessage.challenges;
            gamesOverviews = sort(gamesOverviews);
            if (mAdapter == null) {
                mAdapter = new StartPageAdapter(getActivity(), R.layout.game_overview, gamesOverviews);
            } else {
                mAdapter.clear();
                mAdapter.addAll(gamesOverviews);
            }

            View root = getView();
            if (root != null) {
                View v = root.findViewById(R.id.noMatches);
                v.setVisibility(View.INVISIBLE);
                if (mListView == null)
                    mListView = (ListView) getView().findViewById(R.id.mMatches);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        GamesOverview g = (GamesOverview) parent.getItemAtPosition(position);

                        StartPageAdapter s = (StartPageAdapter) parent.getAdapter();
                        if (MainActivity.DEBUG)
                            Log.d(MainActivity.TAG, "GameID in intent = " + g.gameID);
                        Intent n = new Intent(s.c, MatchActivity.class);
                        n.putExtra("gameID", g.gameID);
                        s.c.startActivity(n);
                    }

                });
                mListView.setAdapter(mAdapter);
                Log.d(MainActivity.TAG,"Chalenges len = " + challenges.size());
                if (challenges.size() > 0) {
                    showChallengeDialog(challenges.get(0));
                }
            }
        } else {
            View root = getView();
            if (root != null) {
                View v = root.findViewById(R.id.noMatches);
                v.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showChallengeDialog(final Challenge challenge) {
        final View dialogView = View.inflate(getActivity(), R.layout.challenge_dialog, null);
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
        Button accept =(Button)dialogView.findViewById(R.id.acceptbutton);
        Button deny =(Button)dialogView.findViewById(R.id.denyButton);
        TextView ct = (TextView) dialogView.findViewById(R.id.challengeFrom);
        if(ct != null) {
            ct.setText("Du har fått en utmaning från " + challenge.OppName);
        }
        if (accept != null) {
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Request(s,((MainActivity)getActivity()).net).execute("AnswerChallenge",challenge.OppID, "accept");
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        revealShow(dialogView, false, dialog);
                    } else {
                        dialogView.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        if (deny != null) {
            deny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Request(s,((MainActivity)getActivity()).net).execute("AnswerChallenge",challenge.OppID, "deny");
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        revealShow(dialogView, false, dialog);
                    } else {
                        dialogView.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    private void showFabDialog(){
        final View dialogView = View.inflate(getActivity(), R.layout.new_game_dialog, null);

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);


        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // call something for API Level 11+te
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
                        m.changeFragment(MainActivity.CHALLENGE_FRIEND);
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
                        m.changeFragment(MainActivity.FIND_FRIEND);
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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



    public void challengeReg() {
        if (challenges != null && challenges.size() > 0) {
            challenges.remove(0);
            if (challenges.size() > 0) {
                showChallengeDialog(challenges.get(1));
            }
        }
    }


    @Override
    public void onRefresh() {
        Request r = new Request(this, mainActivity.net);
        r.execute("StartMessage");
    }


}
