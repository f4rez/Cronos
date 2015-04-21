package com.example.awesomeness.awesomeness.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.awesomeness.awesomeness.Adapters.FriendAdapter;
import com.example.awesomeness.awesomeness.Items.Friend;
import com.example.awesomeness.awesomeness.Json.Decode;
import com.example.awesomeness.awesomeness.MainActivity;
import com.example.awesomeness.awesomeness.Net.Request;
import com.example.awesomeness.awesomeness.R;

import java.util.ArrayList;

/**
 * Created by josef on 2015-04-21.
 */
public class ChallengeFriendFragment extends BaseFragment {
     private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.challenge_friend_fragment, container, false);
        setTargetFragment(this,0);

        mainActivity = (MainActivity)getActivity();
        Request r = new Request(this, mainActivity.net);
        r.execute("FriendList");
        return rootView;
    }

    @Override
    public int getTitleResourceId() {
        return MainActivity.MAINPAGE;
    }


    public void showFriends(String json){
        Decode d = new Decode();
        ArrayList<Friend> friends = d.decodeFriendList(json);
        GridView gridView = (GridView) getView().findViewById(R.id.friendGridView);
        FriendAdapter f = new FriendAdapter(getActivity(),R.layout.challenge_friend_griditem);
        f.addAll(friends);
        gridView.setAdapter(f);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity m = (MainActivity) getActivity();
                FriendAdapter f = (FriendAdapter) parent.getAdapter();
                Request r = new Request(getTargetFragment(),m.net );
                r.execute("FriendChallenge", f.getItem(position).Id);
            }
        });
    }

    public void challengedFriend(){
        Toast.makeText(getActivity(), "Challenged Friend", Toast.LENGTH_SHORT);
    }
}
