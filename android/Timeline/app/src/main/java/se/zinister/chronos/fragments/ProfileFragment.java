package se.zinister.chronos.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import se.zinister.chronos.Adapters.FriendAdapter;
import se.zinister.chronos.Items.Friend;
import se.zinister.chronos.Items.Profile;
import se.zinister.chronos.Json.Decode;
import se.zinister.chronos.MainActivity;
import se.zinister.chronos.Net.Request;
import se.zinister.chronos.R;

/**
 * Created by josef on 2015-05-19.
 *
 */
public class ProfileFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);
        MainActivity m = (MainActivity) getActivity();
        new Request(this, m.net).execute("FriendListProfile");
        return rootView;
    }

    EditText e;


    @Override
    public int getTitleResourceId() {
        return MainActivity.PROFILE;
    }


    public void callback(String json) {
        Profile profile = new Decode().decodeProfile(json);
        if (profile != null) {
            setUpWebView(profile.won, profile.lost, profile.draw);
            showFriends(profile.friends);
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    public void setUpWebView(int won, int lost, int draw) {
        View rootView = getView();
        if (rootView != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels/3;
            int height = metrics.heightPixels/3;
            WebView webView = (WebView) rootView.findViewById(R.id.profileWebView);
            if (webView != null) {
                String content = "<html>"
                        + "  <head>"
                        + "    <script type=\"text/javascript\" src=\"jsapi.js\"></script>"
                        + "    <script type=\"text/javascript\">"
                        + "      google.load(\"visualization\", \"1.1\", {packages:[\"corechart\"]});"
                        + "      google.setOnLoadCallback(drawChart);"
                        + "      function drawChart() {"
                        + "        var data = google.visualization.arrayToDataTable(["
                        + "          ['Result', 'Sales'],"
                        + "          ['Won',  " + won + "],"
                        + "          ['Lost', " + lost + "],"
                        + "          ['Draw',  " + draw + "]"
                        + "        ]);"
                        + "        var options = {"
                        + "          'width':" +width
                        + "          ,'height':" + (height/3)
                        + "        };"
                        + "        var chart = new google.visualization.PieChart(document.getElementById('pie_chart'));"
                        + "        chart.draw(data,options);"
                        + "      }"
                        + "    </script>"
                        + "  </head>"
                        + "  <body>"
                        + "    <div id=\"pie_chart\" style=\"width: " + width + "px; height: " + (height/3) + "px;\"></div>"
                        + "  </body>" + "</html>";

                WebSettings webSettings = webView.getSettings();
                webSettings.setDisplayZoomControls(false);
                webSettings.setSupportZoom(false);
                webSettings.setJavaScriptEnabled(true);
                webView.requestFocusFromTouch();
                webView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return true;
                    }
                });
                webView.setLongClickable(false);
                webView.setHapticFeedbackEnabled(false);
                webView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return (event.getAction() == MotionEvent.ACTION_MOVE);
                    }
                });
                webView.getMeasuredWidth();
                webView.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "utf-8", null);
            }
        }
    }


    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public void showFriends(ArrayList<Friend> friends) {
        View root = getView();
        if (root != null) {
            GridView gridView = (GridView) root.findViewById(R.id.profileGridView);
            if (gridView != null) {
                FriendAdapter f = new FriendAdapter(getActivity(), R.layout.challenge_friend_griditem);
                f.addAll(friends);
                gridView.setAdapter(f);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        FriendAdapter f = (FriendAdapter) parent.getAdapter();
                        ImageView imageView = (ImageView) view.findViewById(R.id.profilePicture);
                        TransitionSet transitionSet = new TransitionSet();
                        transitionSet.addTransition(new ChangeImageTransform());
                        transitionSet.addTransition(new ChangeBounds());
                        transitionSet.setDuration(300);

                        Fragment fragment2 = new FriendFragment();
                        fragment2.setSharedElementEnterTransition(transitionSet);
                        fragment2.setSharedElementReturnTransition(transitionSet);
                        Fade fade = new Fade();
                        fade.setStartDelay(300);
                        fragment2.setEnterTransition(fade);

                        Friend friend = f.getItem(position);

                        Intent i = getActivity().getIntent();
                        i.putExtra("FriendName", friend.Name);
                        ((MainActivity)getActivity()).setmTitle(friend.Name);
                        Bitmap bitmap = drawableToBitmap(imageView.getDrawable());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] b = baos.toByteArray();
                        i.putExtra("FriendPicture", b);
                        i.putExtra("FriendID", friend.Id);
                        i.putExtra("won", friend.won);
                        i.putExtra("draw", friend.draw);
                        i.putExtra("lost", friend.lost);
                        i.putExtra("isFirend", friend.isFriend);

                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, fragment2)
                                .addToBackStack("Stack")
                                .addSharedElement(imageView, "profilePicture2")
                                .commit();
                    }
                });
            }
        }
    }
}
