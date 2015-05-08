package se.zinister.timeline.Adapters;

import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import se.zinister.timeline.Items.DrawerItem;
import se.zinister.timeline.R;


/**
 * Created by josef on 2015-04-21.
 */
public class DialogAdapter extends ArrayAdapter<DrawerItem> {

    public Fragment caller;

    public DialogAdapter(Fragment fragment, int resource) {
        super(fragment.getActivity(), resource);
        caller = fragment;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.drawer_list_item, null);

        }

        DrawerItem p = getItem(position);

        if (p != null) {

            ImageView img = (ImageView) v.findViewById(R.id.picture);
            TextView tt1 = (TextView) v.findViewById(R.id.selectionText);


            if (img != null) {
                img.setImageDrawable(caller.getActivity().getResources().getDrawable(p.picID));
            }
            if (tt1 != null) {

                tt1.setText(p.name);
            }

        }

        return v;

    }
}
