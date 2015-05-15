package se.zinister.timeline.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import se.zinister.timeline.MainActivity;
import se.zinister.timeline.Match.GamesOverview;
import se.zinister.timeline.R;


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
    private static final int TYPE_SECTION_FINISHED_GAME = 3;
    private static final int TYPE_FINISHED_GAME = 4;

    // since you only have 2 types
    private static final int TYPE_MAX_COUNT = 5;

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
            case TYPE_SECTION_FINISHED_GAME:
                return TYPE_SECTION_FINISHED_GAME;
            case TYPE_FINISHED_GAME:
                return TYPE_FINISHED_GAME;
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

            case TYPE_FINISHED_GAME:
                if (v == null) {

                    LayoutInflater vi;
                    vi = LayoutInflater.from(getContext());
                    v = vi.inflate(R.layout.game_overview, null);

                }
                GamesOverview q = getItem(position);

                if (q != null) {
                    TextView tt1 = (TextView) v.findViewById(R.id.mainText);
                    TextView tt2 = (TextView) v.findViewById(R.id.score);
                    ImageView img = (ImageView) v.findViewById(R.id.oppPicture);
                    q.changePicSize(150);
                    if (img != null && q.opponentPic != null) {
                        Picasso.with(getContext()).load(q.opponentPic).placeholder(R.mipmap.ic_launcher).into(img);
                    }

                    if (tt1 != null && tt2 != null) {
                        if (q.opponentPoint>q.myPoint) {
                            tt1.setText(q.opponentName + " vann mot dig");
                        }
                        if (q.myPoint>q.opponentPoint) {
                            tt1.setText("Du vann mot " + q.opponentName);
                        }
                        if (q.opponentPoint == q.myPoint) tt1.setText("Det blev oavgjort mellan dig och " + q.opponentName);
                        tt2.setText(MainActivity.MY_NAME + " " + q.myPoint + " - " + q.opponentPoint + " " + q.opponentName);
                    }
                }


                return v;

            case TYPE_SECTION_FINISHED_GAME:
                if (v == null) {
                    LayoutInflater vi;
                    vi = LayoutInflater.from(getContext());
                    v = vi.inflate(R.layout.start_page_section_divider, null);
                }
                TextView textView3 = (TextView)v.findViewById(R.id.section);
                textView3.setText(c.getString(R.string.finished_games));

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)textView3.getLayoutParams();
                params.setMargins(16, 30, 0, 0); //substitute parameters for left, top, right, bottom
                textView3.setLayoutParams(params);
                return v;
            case TYPE_GAME:

                if (v == null) {

                    LayoutInflater vi;
                    vi = LayoutInflater.from(getContext());
                    v = vi.inflate(R.layout.game_overview, null);

                }
                q = getItem(position);

                if (q != null) {


                    TextView tt1 = (TextView) v.findViewById(R.id.mainText);
                    TextView tt2 = (TextView) v.findViewById(R.id.score);
                    ImageView img = (ImageView) v.findViewById(R.id.oppPicture);
                    q.changePicSize(150);
                    if (img != null) {
                        if (q.opponentPic.equals("")) {
                            img.setImageDrawable(c.getResources().getDrawable(R.mipmap.ic_launcher));
                        } else
                        Picasso.with(getContext()).load(q.opponentPic).placeholder(R.mipmap.ic_launcher).into(img);
                    }
                    if (tt1 != null) {
                        if(q.myTurn)
                            if(!q.opponentName.equals(""))
                                tt1.setText("Det är din tur mot " + q.opponentName + " i omgång " + q.numberOfTurns);
                            else tt1.setText("Det är din tur mot slumpad spelare i omgång " + q.numberOfTurns);
                        else
                        if (!q.opponentName.equals(""))
                            tt1.setText("Det är " + q.opponentName + "s tur" + " i omgång " + q.numberOfTurns);
                        else  tt1.setText("Det är slumpad spelares tur i omgång " + q.numberOfTurns);
                    }

                    if (tt2 != null)  {
                        if (!q.opponentName.equals("")) {
                            if (q.myPoint == -1 && q.opponentPoint == -1) tt2.setText(MainActivity.MY_NAME +  " 0 - 0 " + q.opponentName);
                            if (q.myPoint == -1 && q.opponentPoint > -1) tt2.setText(MainActivity.MY_NAME + " 0 - " + q.opponentPoint + " " + q.opponentName);
                            if (q.myPoint > -1 && q.opponentPoint == -1) tt2.setText(MainActivity.MY_NAME + " " + q.myPoint + " - 0 " + q.opponentName);
                            if (q.myPoint > -1 && q.opponentPoint > -1) tt2.setText(MainActivity.MY_NAME + " " + q.myPoint + " - " + q.opponentPoint + " " + q.opponentName);
                        }
                        else {
                            if (q.myPoint > -1) tt2.setText(MainActivity.MY_NAME + " " + q.myPoint + " - 0 slumpad spelare");
                            else tt2.setText(MainActivity.MY_NAME + " 0 - 0 slumpad spelare");
                        }

                    }

                }

                return v;
        }

        return convertView;
    }









}
