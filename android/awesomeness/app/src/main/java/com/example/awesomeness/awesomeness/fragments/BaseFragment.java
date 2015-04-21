package com.example.awesomeness.awesomeness.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Josef on 2015-02-05.
 */
public abstract class BaseFragment extends Fragment {

    //private static final String ARG_SECTION_NUMBER = "section_number";
    public static final int TITLE_NONE = -1;


    protected void setTitle(){
        int titleResId = getTitleResourceId();
        if (titleResId != TITLE_NONE)
            getActivity().setTitle(getTitleResourceId());
    }

    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
                                             Bundle savedInstanceState);

    public abstract int getTitleResourceId();


}
