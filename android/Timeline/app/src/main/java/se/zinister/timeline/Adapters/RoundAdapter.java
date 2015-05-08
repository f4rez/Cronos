package se.zinister.timeline.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import se.zinister.timeline.Items.Game;
import se.zinister.timeline.R;


/**
 * Created by enigma on 2015-04-21.
 */
public class RoundAdapter extends ArrayAdapter<Game.Round> {


    public RoundAdapter(Context context, int resource) {
        super(context, resource);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.matchstatistics_item, null);

        }

        TextView p1r = (TextView) v.findViewById(R.id.player1_roundNumber);
        TextView rsb = (TextView) v.findViewById(R.id.round_scoreBox);
        TextView p2r = (TextView) v.findViewById(R.id.player2_roundNumber);

        Game.Round r = getItem(position);
        if (p1r != null) p1r.setText("Runda " + (position+1));
        if (p2r != null) p2r.setText("Runda " + (position+1));
        if (rsb != null) {
            if (r.myRoundScore == -1 && r.oppRoundScore == -1) rsb.setText("0 - 0");
            if (r.myRoundScore > -1 && r.oppRoundScore == -1) rsb.setText( r.myRoundScore + " - 0");
            if (r.myRoundScore == -1 && r.oppRoundScore > -1) rsb.setText( "0 - " + r.oppRoundScore);
            if (r.myRoundScore > -1 && r.oppRoundScore > -1) rsb.setText( r.myRoundScore + " - " + r.oppRoundScore);
        }


        return v;

    }
}