package se.zinister.chronos.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import se.zinister.chronos.Question.Question;
import se.zinister.chronos.R;


/**
 * Created by josef on 2015-04-14.
 */
public class MatchAdapter extends ArrayAdapter<Question> {

    private static final int UNLOCKED = 0;
    private static final int LOCKED = 1;

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
        Question q = getItem(position);
        if (q.locked) {


            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            View v = vi.inflate(R.layout.list_item, null);
            v.setBackgroundColor(mContext.getResources().getColor(R.color.white));


            if (q != null) {
                TextView tt1 = (TextView) v.findViewById(R.id.question);
                TextView year = (TextView) v.findViewById(R.id.year);
                if (tt1 != null) {
                    tt1.setTextColor(mContext.getResources().getColor(R.color.dark_grey_text));
                    tt1.setText(q.question);

                }
                if (year != null) {
                    year.setText(q.year.toString());
                    if(q.done) {
                        Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
                        year.setAnimation(fadeInAnimation);
                        year.setVisibility(View.VISIBLE);
                    }
                    else year.setVisibility(View.INVISIBLE);
                    year.setTextColor(mContext.getResources().getColor(R.color.dark_grey_text));
                }
            }

            return v;
        } else {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            View v = vi.inflate(R.layout.list_item, null);
            v.setBackgroundColor(mContext.getResources().getColor(R.color.primary_color));
            TextView tt1 = (TextView) v.findViewById(R.id.question);
            if (tt1 != null) {
                tt1.setText(mContext.getString(R.string.put_event_in_order));
                tt1.setTextColor(mContext.getResources().getColor(R.color.white));
            }

            ImageView i = (ImageView) v.findViewById(R.id.drawerPicture);
            if (i!=null) {
                i.setVisibility(View.INVISIBLE);
            }

            TextView year = (TextView) v.findViewById(R.id.year);
            if (year != null) {
                year.setText(q.year.toString());
                if(q.done) year.setVisibility(View.VISIBLE);
                else year.setVisibility(View.INVISIBLE);
                year.setTextColor(mContext.getResources().getColor(R.color.dark_grey_text));
            }
            return v;

        }
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
        return android.os.Build.VERSION.SDK_INT < 20;

    }


    public void addItem(Question q) {
        mIdMap.put(q, mIdMap.size());
    }
}