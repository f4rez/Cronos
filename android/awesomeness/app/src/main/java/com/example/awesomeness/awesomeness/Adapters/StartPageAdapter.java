package com.example.awesomeness.awesomeness.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.awesomeness.awesomeness.MainActivity;
import com.example.awesomeness.awesomeness.Match.GamesOverview;
import com.example.awesomeness.awesomeness.R;

import java.util.List;

/**
 * Created by josef on 2015-04-14.
 */
public class StartPageAdapter extends ArrayAdapter <GamesOverview>  {
    public Context c;

    public StartPageAdapter(Context context, int textViewResourceId, List<GamesOverview> objects) {
        super(context, textViewResourceId, objects);
        c = context;
    }

    private static final int TYPE_SECTION_MY_TURN = 0;
    private static final int TYPE_GAME = 1;
    private static final int TYPE_SECTION_OPP_TURN = 2;

    // since you only have 2 types
    private static final int TYPE_MAX_COUNT = 3;

    @Override
    public int getItemViewType(int position) {
        // your list object should have a getter to tell what type it is
        switch (getItem(position).type) {
            case TYPE_GAME:
                return TYPE_GAME;
            case TYPE_SECTION_MY_TURN:
                return TYPE_SECTION_MY_TURN;
            case TYPE_SECTION_OPP_TURN:
                return TYPE_SECTION_OPP_TURN;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);

        View v = convertView;
        switch (type) {
            case TYPE_SECTION_MY_TURN:
                if (v == null) {
                    LayoutInflater vi;
                    vi = LayoutInflater.from(getContext());
                    v = vi.inflate(R.layout.start_page_section_divider, null);
                }

                TextView textView = (TextView)v.findViewById(R.id.section);
                textView.setText(c.getString(R.string.my_turn));

                return v;
            case TYPE_SECTION_OPP_TURN:

                if (v == null) {
                    LayoutInflater vi;
                    vi = LayoutInflater.from(getContext());
                    v = vi.inflate(R.layout.start_page_section_divider, null);
                }
                TextView textView2 = (TextView)v.findViewById(R.id.section);
                textView2.setText(c.getString(R.string.opp_turn));
                return v;
            case TYPE_GAME:

                if (v == null) {

                    LayoutInflater vi;
                    vi = LayoutInflater.from(getContext());
                    v = vi.inflate(R.layout.game_overview, null);

                }
                GamesOverview q = getItem(position);

                if (q != null) {


                    TextView tt1 = (TextView) v.findViewById(R.id.mainText);
                    TextView tt2 = (TextView) v.findViewById(R.id.score);
                    if (tt1 != null) {
                        if(q.myTurn)
                            if(q.opponentName != null)
                                tt1.setText("Det är din tur mot " + q.opponentName + " i omgång " + q.numberOfTurns);
                            else tt1.setText("Det är din tur mot slumpad spelare i omgång " + q.numberOfTurns);
                        else
                        if (q.opponentName != null)
                            tt1.setText("Det är " + q.opponentName + "s tur" + " i omgång " + q.numberOfTurns);
                        else  tt1.setText("Det är slumpad spelares tur i omgång " + q.numberOfTurns);
                    }

                    if (tt2 != null)  {
                        if (q.opponentName != null)
                            tt2.setText(MainActivity.MY_NAME + " " + q.myPoint + " - " + q.opponentPoint +  " "+  q.opponentName);
                        else tt2.setText(MainActivity.MY_NAME + " " + q.myPoint + " - 0 slumpad spelare");

                    }

                }

                return v;
        }

        return convertView;
    }









}
