package com.example.awesomeness.awesomeness.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.awesomeness.awesomeness.Items.DrawerItem;
import com.example.awesomeness.awesomeness.Items.Friend;
import com.example.awesomeness.awesomeness.R;

/**
 * Created by josef on 2015-04-21.
 */
public class FriendAdapter extends ArrayAdapter<Friend> {
    private Context mContext;


    public FriendAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.drawer_list_item, null);

        }

        Friend p = getItem(position);

        if (p != null) {

            ImageView img = (ImageView) v.findViewById(R.id.picture);
            TextView tt1 = (TextView) v.findViewById(R.id.selectionText);


            if (img != null) {
                img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_action_new));
            }
            if (tt1 != null) {

                tt1.setText(p.Name);
            }

        }

        return v;

    }

}
