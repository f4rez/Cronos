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

import java.util.HashMap;
import java.util.List;

/**
 * Created by josef on 2015-04-14.
 */
public class StartPageAdapter extends ArrayAdapter <GamesOverview>  {
    public Context c;
    public HashMap<Integer, Integer> mIdMap = new HashMap();


    public StartPageAdapter(Context context, int textViewResourceId, List<GamesOverview> objects) {
        super(context, textViewResourceId, objects);
        c = context;
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(i,objects.get(i).gameID);
        }

    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

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









}
