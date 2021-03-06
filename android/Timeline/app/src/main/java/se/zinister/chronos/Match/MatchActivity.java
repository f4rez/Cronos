package se.zinister.chronos.Match;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import se.zinister.chronos.Items.Game;
import se.zinister.chronos.MainActivity;
import se.zinister.chronos.Net.NetRequests;
import se.zinister.chronos.R;
import se.zinister.chronos.fragments.BaseFragment;
import se.zinister.chronos.fragments.MatchFragment;
import se.zinister.chronos.fragments.MatchStatistics;


/**
 * Created by enigma on 2015-04-14.
 */
public class MatchActivity extends AppCompatActivity {

    public NetRequests net;
    public static int gameID;
    public CharSequence mTitle;
    public Game game;
    private Toolbar mToolbar;



    public static final int MATCHPAGE = 100;
    public static final int STATISTICS = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mToolbar.setFitsSystemWindows(true);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        ActionBar a = getSupportActionBar();
        if (a!= null) {
            a.setHomeButtonEnabled(true);
            a.setDisplayHomeAsUpEnabled(true);
            a.setDisplayHomeAsUpEnabled(true);
        }
        Bundle extras = getIntent().getExtras();
        gameID = extras.getInt("gameID");
        if (MainActivity.DEBUG) Log.d(MainActivity.TAG, "GameID in MatchActivity: " + gameID);
        net = new NetRequests(MainActivity.HOST);

        BaseFragment b = selectFragment(STATISTICS);
        openFragment(b);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.showInfoMatchActivity:
                MatchFragment m = (MatchFragment)getFragmentManager().findFragmentById(R.id.matchContainer);
                m.showInfo();
        }
        return (super.onOptionsItemSelected(menuItem));
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
