package com.example.awesomeness.awesomeness.Match;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.example.awesomeness.awesomeness.Adapters.DynamicListView;
import com.example.awesomeness.awesomeness.Adapters.MatchAdapter;
import com.example.awesomeness.awesomeness.MainActivity;
import com.example.awesomeness.awesomeness.Net.GameRequests;
import com.example.awesomeness.awesomeness.Net.NetRequests;
import com.example.awesomeness.awesomeness.R;
import com.example.awesomeness.awesomeness.fragments.BaseFragment;
import com.example.awesomeness.awesomeness.fragments.MatchFragment;





/**
 * Created by enigma on 2015-04-14.
 */
public class MatchActivity extends ActionBarActivity{

    public NetRequests net;
    int gameID;
    private CharSequence mTitle;


    public static final int MATCHPAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        net = new NetRequests("192.168.43.87:8080",false);
        BaseFragment b = selectFragment(MATCHPAGE);
        openFragment(b);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    private void openFragment(BaseFragment baseFragment) {
        if (MainActivity.DEBUG) Log.d(MainActivity.TAG, "in openFragment");
        if (baseFragment != null) {
            if (baseFragment.getTitleResourceId() > 0) {
                if (MainActivity.DEBUG) Log.d(MainActivity.TAG, "getTitleResourceID = " + baseFragment.getTitleResourceId());
                onSectionAttached(baseFragment.getTitleResourceId());
            } else {
                if (MainActivity.DEBUG) Log.d(MainActivity.TAG, "getTitleResourceID = " + baseFragment.getTitleResourceId());
            }
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.matchContainer, baseFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            if (MainActivity.DEBUG) Log.d(MainActivity.TAG, "basefragment = null");
        }

    }
    private BaseFragment selectFragment(int position) {
        BaseFragment baseFragment = null;

        switch (position) {
            case MATCHPAGE:
                baseFragment = new MatchFragment();

                break;


        }
        return baseFragment;
    }

    public void onSectionAttached(int number) {
        if (MainActivity.DEBUG) Log.d(MainActivity.TAG, "onSectionAttached = " + number);
        switch (number) {
            case MATCHPAGE:
                mTitle = getString(R.string.title_section1);
                break;
        }
        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setTitle(mTitle);
        }
    }


}
