package com.example.awesomeness.awesomeness.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.awesomeness.awesomeness.MainActivity;
import com.example.awesomeness.awesomeness.R;

/**
 * Created by josef on 2015-04-23.
 */
public class FriendFragment extends  BaseFragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.friend_fragment, container, false);

        Bundle extras = getActivity().getIntent().getExtras();
        byte[] b = extras.getByteArray("FriendPicture");
        String name = extras.getString("FriendName");
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.profilePicture);
        TextView nametextview = (TextView) rootView.findViewById(R.id.Name);
        nametextview.setText(name);
        imageView.setImageBitmap(bmp);
        extras.remove("FriendPicture");
        extras.remove("FriendName");

        return rootView;
    }

    @Override
    public int getTitleResourceId() {
        return ((MainActivity)getActivity()).FIND_FRIEND;
    }
}
