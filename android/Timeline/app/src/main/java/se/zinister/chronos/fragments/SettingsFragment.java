package se.zinister.chronos.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.zinister.chronos.MainActivity;
import se.zinister.chronos.R;

/**
 * Created by Josef on 2015-05-20.
 *
 */
public class SettingsFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings_fragment, container, false);
        return rootView;
    }

    @Override
    public int getTitleResourceId() {
        return MainActivity.SETTINGS;
    }
}
