package com.example.awesomeness.awesomeness.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.awesomeness.awesomeness.Match.GamesOverview;
import com.example.awesomeness.awesomeness.Question.Question;
import com.example.awesomeness.awesomeness.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by josef on 2015-04-14.
 */
public class MatchAdapter extends ArrayAdapter<Question> {

    Context mContext;
    final int INVALID_ID = -1;
    public HashMap<Question, Integer> mIdMap = new HashMap<>();

    public MatchAdapter(Context context, int resource, ArrayList<Question> q) {
        super(context, resource, q);
        mContext = context;
        for (int i = 0; i < q.size(); ++i) {
            mIdMap.put(q.get(i), i);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item, null);
        }
        Question q = getItem(position);
        if (q != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.question);
            TextView year = (TextView) v.findViewById(R.id.year);
            if (tt1 != null) {

                tt1.setText(q.question);
            }
            if (year != null) {
                year.setText(q.year.toString());
                year.setVisibility(View.INVISIBLE);
            }
        }
        return v;

    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        Question item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    public void addItem(Question q) {
        mIdMap.put(q, mIdMap.size());
    }
}