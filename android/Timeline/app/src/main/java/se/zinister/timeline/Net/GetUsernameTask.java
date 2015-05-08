package se.zinister.timeline.Net;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

import se.zinister.timeline.fragments.ChallengeFriendFragment;
import se.zinister.timeline.fragments.FindUsersFragment;
import se.zinister.timeline.fragments.MatchFragment;
import se.zinister.timeline.fragments.MatchStatistics;
import se.zinister.timeline.fragments.StartPageFragment;

/**
 * Created by josef on 2015-05-07.
 */
public class GetUsernameTask extends AsyncTask<Void, Void, String> {
    Activity mActivity;
    String mScope;
    String mEmail;

    public GetUsernameTask(Activity activity, String name, String scope) {
        this.mActivity = activity;
        this.mScope = scope;
        this.mEmail = name;
    }

    /**
     * Executes the asynchronous job. This runs when you call execute()
     * on the AsyncTask instance.
     */
    @Override
    protected String doInBackground(Void... params) {
        try {
            Log.d("AAAAAAAAAAAAAAAA", "aaa");
            String token = fetchToken();
            if (token != null) {
                Log.d("AAAAAAAAAAAAAAAA", "aaa");
                // Insert the good stuff here.
                // Use the token to access the user's Google data.
            }
        } catch (IOException e) {
            // The fetchToken() method handles Google-specific exceptions,
            // so this indicates something went wrong at a higher level.
            // TIP: Check for network connectivity before starting the AsyncTask.

        }
        return null;
    }

    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    protected String fetchToken() throws IOException {
        try {
            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
        } catch (UserRecoverableAuthException userRecoverableException) {
            // GooglePlayServices.apk is either old, disabled, or not present
            // so we need to show the user some UI in the activity to recover.
            //mActivity.handleException(userRecoverableException);
        } catch (GoogleAuthException fatalException) {
            // Some other type of unrecoverable exception has occurred.
            // Report and log the error as appropriate for your app.
        }
        return null;
    }

    @Override
    protected void onPostExecute(String returned) {
    Log.d("AAAAAAA","aaaa");

    }
}
