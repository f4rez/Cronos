package se.zinister.chronos.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import se.zinister.chronos.Items.DrawerItem;
import se.zinister.chronos.R;


/**
 * Created by Josef on 2015-04-20.
 */
public class DrawerAdapter extends ArrayAdapter<DrawerItem>{

    public Context mContext;

    public DrawerAdapter(Context context, int resource) {
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

        DrawerItem p = getItem(position);

        if (p != null) {

            ImageView img = (ImageView) v.findViewById(R.id.picture);
            TextView tt1 = (TextView) v.findViewById(R.id.selectionText);


            if (img != null) {
                img.setImageDrawable(mContext.getResources().getDrawable(p.picID));
            }
            if (tt1 != null) {

                tt1.setText(p.name);
            }

        }

        return v;

    }
}
