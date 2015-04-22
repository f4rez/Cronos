package com.example.awesomeness.awesomeness.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.awesomeness.awesomeness.Items.DrawerItem;
import com.example.awesomeness.awesomeness.Items.RoundItem;
import com.example.awesomeness.awesomeness.R;

/**
 * Created by enigma on 2015-04-21.
 */
public class RoundAdapter extends ArrayAdapter<RoundItem> {


    public RoundAdapter(Context context, int resource) {
        super(context, resource);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.drawer_list_item, null);

        }

        RoundItem r = getItem(position);


        return v;

    }
}