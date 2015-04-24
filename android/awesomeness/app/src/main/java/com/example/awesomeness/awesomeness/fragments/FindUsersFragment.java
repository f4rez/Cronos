package com.example.awesomeness.awesomeness.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awesomeness.awesomeness.Adapters.FriendAdapter;
import com.example.awesomeness.awesomeness.Items.Friend;
import com.example.awesomeness.awesomeness.Json.Decode;
import com.example.awesomeness.awesomeness.MainActivity;
import com.example.awesomeness.awesomeness.Net.Request;
import com.example.awesomeness.awesomeness.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by josef on 2015-04-21.
 */
public class FindUsersFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.find_users_fragment, container, false);
        ActionBar a= ((MainActivity)getActivity()).getSupportActionBar();
        ((MainActivity)getActivity()).setmToolbarTransperent();
        setTargetFragment(this, 0);
        EditText e = (EditText)rootView.findViewById(R.id.searchFriends);
        e.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                MainActivity m = (MainActivity) getActivity();
                Request r = new Request(getTargetFragment(),m.net);
                r.execute("Search", "Name", v.getText().toString());
                return true;
            }
        });
        return rootView;
    }

    @Override
    public int getTitleResourceId() {
        return MainActivity.FIND_FRIEND;
    }


    public void showResult(String json) {
        Decode d = new Decode();
        ArrayList<Friend> friends = d.decodeFriendList(json);
        GridView gridView = (GridView) getView().findViewById(R.id.gridViewFindFriend);
        FriendAdapter f = new FriendAdapter(getActivity(),R.layout.challenge_friend_griditem);
        f.addAll(friends);
        gridView.setAdapter(f);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendAdapter f = (FriendAdapter) parent.getAdapter();
                MainActivity m = (MainActivity) getActivity();

                ImageView imageView = (ImageView) view.findViewById(R.id.profilePicture);
                TextView textView = (TextView) view.findViewById(R.id.selectionText);
                TransitionSet transitionSet = new TransitionSet();
                transitionSet.addTransition(new ChangeImageTransform());
                transitionSet.setDuration(300);

                Fragment fragment2 = new FriendFragment();
                fragment2.setSharedElementEnterTransition(transitionSet);
                fragment2.setSharedElementReturnTransition(transitionSet);
                Fade fade = new Fade();
                fade.setStartDelay(300);
                fragment2.setEnterTransition(fade);

                Friend friend = f.getItem(position);

                Intent i = getActivity().getIntent();
                i.putExtra("FriendName", friend.Name );

                Bitmap bitmap = drawableToBitmap(imageView.getDrawable());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                i.putExtra("FriendPicture",b);
                i.putExtra("FriendId",friend.Id);


                getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment2)
                        .addToBackStack("Stack")
                        .addSharedElement(imageView, "profilePicture2")
                        .addSharedElement(textView,"friendName")
                        .commit();
            }
        });
    }


    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public void addedFriend() {
        Toast.makeText(getActivity(),"Added friend",Toast.LENGTH_SHORT);
    }
}
