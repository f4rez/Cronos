package com.example.awesomeness.awesomeness.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
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
public class FindUsersFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.find_users_fragment, container, false);
        setTargetFragment(this,0);
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
                Request r = new Request(getTargetFragment(),m.net);
                r.execute("Friend", "add", f.getItem(position).Id);
            }
        });
    }

    public void addedFriend() {
        Toast.makeText(getActivity(),"Added friend",Toast.LENGTH_SHORT);
    }
}
