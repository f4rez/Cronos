package se.zinister.timeline.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import se.zinister.timeline.Items.Friend;
import se.zinister.timeline.R;


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
            v = vi.inflate(R.layout.challenge_friend_griditem, null);

        }

        Friend p = getItem(position);

        if (p != null) {

            ImageView img = (ImageView) v.findViewById(R.id.profilePicture);
            TextView tt1 = (TextView) v.findViewById(R.id.selectionText);


            if (img != null && p.PictureLink != null && !p.PictureLink.equals("")) {
                Picasso.with(getContext()).load(p.PictureLink).into(img);
            }
            if (tt1 != null) {

                tt1.setText(p.Name);
            }

        }

        return v;

    }

}
