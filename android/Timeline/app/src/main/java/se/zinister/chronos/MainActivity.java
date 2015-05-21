package se.zinister.chronos;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;


import se.zinister.chronos.Items.User;
import se.zinister.chronos.Json.Decode;
import se.zinister.chronos.Net.NetRequests;
import se.zinister.chronos.Net.Request;
import se.zinister.chronos.fragments.BaseFragment;
import se.zinister.chronos.fragments.ChallengeFriendFragment;
import se.zinister.chronos.fragments.FindUsersFragment;
import se.zinister.chronos.fragments.ProfileFragment;
import se.zinister.chronos.fragments.SettingsFragment;
import se.zinister.chronos.fragments.StartPageFragment;



public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    public NetRequests net;

    public GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;



    public static String TAG = "Zinister";
    public static boolean DEBUG = true;
    public static final int MAINPAGE = 100;
    public static final int SETTINGS = 101;
    public static final int FIND_FRIEND = 106;
    public static final int PROFILE = 102;
    public static final int CHALLENGE_FRIEND = 105;
    public static final int FRIEND = 103;
    public static final int LOGIN = 104;
    private static final int RC_SIGN_IN = 0;


    public SharedPreferences userDetails;


    public static String MY_NAME;
    public static final String HOST = "calcium-firefly-93808.appspot.com";
    public static final String FULL_HOST = "https://calcium-firefly-93808.appspot.com";
    public static  String LOGOUT;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getString(R.string.drawer_item_1);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        net = new NetRequests(HOST);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .build();
        }

        String cookies = android.webkit.CookieManager.getInstance().getCookie("https://" +HOST +"/startMess");
        if(cookies == null) {
            Intent n = new Intent(this, LoginActivity.class);
            n.putExtra("url", FULL_HOST);
            startActivityForResult(n, LOGIN);
        }
        if (MY_NAME == null) {

            String tmp = userDetails.getString("MY_NAME", "noName");
            if(DEBUG) Log.d(TAG, tmp);
            assert tmp != null;
            if (!tmp.equals("noName")) MY_NAME = tmp;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (DEBUG) Log.d(TAG, "onActivityResult requestcode" + requestCode + ", resultcode " + resultCode );
        if (requestCode == LOGIN) {
            if (resultCode == RESULT_OK) {
                mNavigationDrawerFragment.setLogout(LOGOUT);
                String tmp = userDetails.getString("MY_NAME", "noName");
                if (!(tmp != null && tmp.equals("noName"))) MY_NAME = tmp;
                else if (!mGoogleApiClient.isConnected()) {
                    onClick(null);
                }
            }
        } if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.reconnect();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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

    public void doneRegister(String json){
        Decode decode = new Decode();
        User u = decode.decodeUser(json);
        SharedPreferences.Editor e = userDetails.edit();
        if (u != null) {
            String pic = u.picture.substring(0, u.picture.indexOf("sz=")) + "sz=500";
            e.putString("MY_NAME", u.name);
            e.putString("MY_PICTURE", pic);
            e.apply();
            MY_NAME = u.name;

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
            case PROFILE:
                baseFragment = new ProfileFragment();
                break;
            case SETTINGS:
                baseFragment = new SettingsFragment();
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
                break;
            case PROFILE:
                mTitle = MY_NAME;
                break;
            case SETTINGS:
                mTitle = getString(R.string.drawer_item_2);
        }
        restoreActionBar();
    }


    public void setmTitle(String s ) {
        mTitle = s;
        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }



    @Override
    public void onConnected(Bundle connectionHint) {
        mSignInClicked = false;
        Person me = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        new Request(this, net).execute("RegisterUser", me.getDisplayName(), me.getImage().getUrl()); //TODO fixa om personen inte ha en bild och namn
        if(me.hasCover()) {
            if (me.getCover().hasCoverPhoto()) mNavigationDrawerFragment.setCoverPhoto(me.getCover().getCoverPhoto().getUrl());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }
    private boolean mSignInClicked;

    public void onClick(View view) {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            mGoogleApiClient.connect();
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult result ) {
        if (!mIntentInProgress) {
            if (mSignInClicked && result.hasResolution()) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                try {
                    result.startResolutionForResult(this, RC_SIGN_IN);
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

}
