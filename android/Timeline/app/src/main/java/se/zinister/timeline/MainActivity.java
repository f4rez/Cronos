package se.zinister.timeline;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import se.zinister.timeline.Net.NetRequests;
import se.zinister.timeline.fragments.BaseFragment;
import se.zinister.timeline.fragments.ChallengeFriendFragment;
import se.zinister.timeline.fragments.FindUsersFragment;
import se.zinister.timeline.fragments.StartPageFragment;



public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks  {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private CharSequence mTitle;
    private DrawerLayout mDrawer;

    public NetRequests net;


    public static String TAG = "Zinister";
    public static boolean DEBUG = true;
    public static final int MAINPAGE = 100;
    public static final int CHALLENGE_FRIEND = 101;
    public static final int FIND_FRIEND = 102;
    public static final int FRIEND = 103;
    public static final int LOGIN = 104;


    public static final String MY_NAME = "Jos@s.se";
    public static final String HOST = "calcium-firefly-93808.appspot.com";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);


        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        net = new NetRequests(HOST);
        Intent n = new Intent(this, LoginActivity.class);
        startActivity(n);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
        return true;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        BaseFragment baseFragment = selectFragment(position);
        openFragment(baseFragment);
    }

    @Override
    public void onBackPressed(){
        BaseFragment b = (BaseFragment)getFragmentManager().findFragmentById(R.id.container);
        switch (b.getTitleResourceId()) {
            case MAINPAGE:
                super.onBackPressed();
                break;
            case CHALLENGE_FRIEND:
                onNavigationDrawerItemSelected(MAINPAGE);
                break;
            case FIND_FRIEND:
                onNavigationDrawerItemSelected(MAINPAGE);
                break;
            case FRIEND:
                onNavigationDrawerItemSelected(FIND_FRIEND);
                break;
        }

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
                baseFragment = new StartPageFragment();
                break;
            case CHALLENGE_FRIEND:
                baseFragment = new ChallengeFriendFragment();
                break;
            case FIND_FRIEND:
                baseFragment = new FindUsersFragment();
                break;
        }
        return baseFragment;
    }

    public void changeFragment(int i) {
        BaseFragment b = selectFragment(i);
        openFragment(b);
    }

    public void onSectionAttached(int number) {
        if (DEBUG) Log.d(TAG, "onSectionAttached = " + number);
        switch (number) {
            case MAINPAGE:
                mTitle = getString(R.string.title_section1);
                break;
            case CHALLENGE_FRIEND:
                mTitle = getString(R.string.title_section2);
                break;
            case FIND_FRIEND:
                mTitle = getString(R.string.title_section3);
        }
        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    public void setmToolbarTransperent() {
        mToolbar.getBackground().setAlpha(0);
    }


}
