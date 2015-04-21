package com.example.awesomeness.awesomeness.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.awesomeness.awesomeness.MainActivity;
import com.example.awesomeness.awesomeness.Net.Request;
import com.example.awesomeness.awesomeness.R;

/**
 * Created by josef on 2015-04-21.
 */
public class FindUsersFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_question, container, false);
        setTargetFragment(this,0);
        EditText e = (EditText)rootView.findViewById(R.id.searchFriends);
        e.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (event.getAction()) {
                    case KeyEvent.KEYCODE_ENTER:
                        MainActivity m = (MainActivity) getActivity();
                        Request r = new Request(getTargetFragment(),m.net);
                        r.execute("Search", v.getText().toString());
                        break;
                }
                return false;
            }
        });
        return rootView;
    }

    @Override
    public int getTitleResourceId() {
        return 0;
    }
}
