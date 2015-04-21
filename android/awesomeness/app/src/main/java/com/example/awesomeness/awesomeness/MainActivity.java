package com.example.awesomeness.awesomeness;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

import com.example.awesomeness.awesomeness.Adapters.StartPageAdapter;
import com.example.awesomeness.awesomeness.Json.Decode;
import com.example.awesomeness.awesomeness.Match.GamesOverview;
import com.example.awesomeness.awesomeness.Match.MatchActivity;
import com.example.awesomeness.awesomeness.Net.NetRequests;
import com.example.awesomeness.awesomeness.Net.Request;
import com.example.awesomeness.awesomeness.fragments.BaseFragment;
import com.example.awesomeness.awesomeness.fragments.MainPageFragment;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private CharSequence mTitle;
    private DrawerLayout mDrawer;

    public NetRequests net;


    public static final String APP_TITLE = "Appen";
    public static String TAG = "Zinister";
    public static boolean DEBUG = true;
    public static final int MAINPAGE = 100;
    public static final int CASE_OFFERS = 101;
    public static final int CASE_MAP = 102;
    public static final int CASE_SCHEDULE = 103;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        net = new NetRequests("192.168.43.87:8080");
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

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if (DEBUG) Log.d(TAG, "enterd onNavigationDrawerItemSelected");
        BaseFragment baseFragment = selectFragment(position);
        if (DEBUG) Log.d(TAG, "before open fragment");
        openFragment(baseFragment);
    }


    private void openFragment(BaseFragment baseFragment) {
        if (DEBUG) Log.d(TAG, "in openFragment");
        if (baseFragment != null) {
            if (baseFragment.getTitleResourceId() > 0) {
                if (DEBUG) Log.d(TAG, "getTitleResourceID = " + baseFragment.getTitleResourceId());
                onSectionAttached(baseFragment.getTitleResourceId());
            } else {
                if (DEBUG) Log.d(TAG, "getTitleResourceID = " + baseFragment.getTitleResourceId());
            }
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.container, baseFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            if (DEBUG) Log.d(TAG, "basefragment = null");
        }

    }
    private BaseFragment selectFragment(int position) {
        BaseFragment baseFragment = null;

        switch (position) {
            case MAINPAGE:
                baseFragment = new MainPageFragment();
                break;


        }
        return baseFragment;
    }

    public void onSectionAttached(int number) {
        if (DEBUG) Log.d(TAG, "onSectionAttached = " + number);
        switch (number) {
            case MAINPAGE:
                mTitle = getString(R.string.title_section1);
                break;
            case CASE_MAP:
                mTitle = getString(R.string.title_section2);
                break;
            case CASE_OFFERS:
                mTitle = getString(R.string.title_section3);
                break;
            case CASE_SCHEDULE:
                mTitle = getString(R.string.title_section4);
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
