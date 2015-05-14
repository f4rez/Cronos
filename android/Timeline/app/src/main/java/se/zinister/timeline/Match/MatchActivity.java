package se.zinister.timeline.Match;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import se.zinister.timeline.Items.Game;
import se.zinister.timeline.MainActivity;
import se.zinister.timeline.Net.NetRequests;
import se.zinister.timeline.R;
import se.zinister.timeline.fragments.BaseFragment;
import se.zinister.timeline.fragments.MatchFragment;
import se.zinister.timeline.fragments.MatchStatistics;


/**
 * Created by enigma on 2015-04-14.
 */
public class MatchActivity extends ActionBarActivity {

    public NetRequests net;
    public static int gameID;
    private CharSequence mTitle;
    public Game game;
    private Toolbar mToolbar;



    public static final int MATCHPAGE = 100;
    public static final int STATISTICS = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        ActionBar a = getSupportActionBar();
        a.setHomeButtonEnabled(true);
        a.setDisplayHomeAsUpEnabled(true);
        a.setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        gameID = extras.getInt("gameID");
        if (MainActivity.DEBUG) Log.d(MainActivity.TAG, "GameID in MatchActivity: " + gameID);
        net = new NetRequests(MainActivity.HOST);

        BaseFragment b = selectFragment(STATISTICS);
        openFragment(b);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
        return true;
    }


    public void changeFragment(int position) {
        // update the main content by replacing fragments
        if (MainActivity.DEBUG) Log.d(MainActivity.TAG, "enterd onNavigationDrawerItemSelected");
        BaseFragment baseFragment = selectFragment(position);
        if (MainActivity.DEBUG) Log.d(MainActivity.TAG, "before open fragment");
        openFragment(baseFragment);
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
            case STATISTICS:
                baseFragment = new MatchStatistics();
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
            case STATISTICS:
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
