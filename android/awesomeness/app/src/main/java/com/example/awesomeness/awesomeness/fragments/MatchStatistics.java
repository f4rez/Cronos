package com.example.awesomeness.awesomeness.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.awesomeness.awesomeness.Match.MatchActivity;
import com.example.awesomeness.awesomeness.Net.Request;
import com.example.awesomeness.awesomeness.R;

/**
 * Created by enigma on 2015-04-21.
 */
public class MatchStatistics extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.match_statistics, container, false);

        MatchActivity m = (MatchActivity) getActivity();
        Request r = new Request(this, m.net);
        r.execute("RoundStatistics", String.valueOf(m.gameID));
        return rootView;
    }

    @Override
    public int getTitleResourceId() {
        return 0;
    }
}
