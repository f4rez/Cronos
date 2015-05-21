package se.zinister.chronos.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import se.zinister.chronos.MainActivity;
import se.zinister.chronos.Net.Request;
import se.zinister.chronos.R;


/**
 * Created by josef on 2015-04-23.
 *
 */
public class FriendFragment extends  BaseFragment{
    String friendID;
    FriendFragment f = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.friend_fragment, container, false);
        Bundle extras = getActivity().getIntent().getExtras();
        byte[] b = extras.getByteArray("FriendPicture");
        String name = extras.getString("FriendName");
        friendID = extras.getString("FriendID");
        int won = extras.getInt("won");
        int draw = extras.getInt("draw");
        int lost = extras.getInt("lost");
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.profilePicture);
        TextView nametextview = (TextView) rootView.findViewById(R.id.Name);
        nametextview.setText(name);
        imageView.setImageBitmap(bmp);
        extras.remove("FriendPicture");
        extras.remove("FriendName");
        WebView webView = (WebView) rootView.findViewById(R.id.firstPie);
        String content = "<html>"
                + "  <head>"
                + "    <script type=\"text/javascript\" src=\"jsapi.js\"></script>"
                + "    <script type=\"text/javascript\">"
                + "      google.load(\"visualization\", \"1.1\", {packages:[\"corechart\"]});"
                + "      google.setOnLoadCallback(drawChart);"
                + "      function drawChart() {"
                + "        var data = google.visualization.arrayToDataTable(["
                + "          ['Result', 'Sales'],"
                + "          ['Won',  "+won+"],"
                + "          ['Lost', "+lost+"],"
                + "          ['Draw',  "+draw+"]"
                + "        ]);"
                + "        var options = {"
                + "          'width':"+webView.getWidth()
                + "          ,'height':"+webView.getHeight()
                + "        };"
                + "        var chart = new google.visualization.PieChart(document.getElementById('pie_chart'));"
                + "        chart.draw(data,options);"
                + "      }"
                + "    </script>"
                + "  </head>"
                + "  <body>"
                + "    <div id=\"pie_chart\" style=\"width: "+ (webView.getWidth()- 100)+"px; height: " + (webView.getHeight()-100) + "px;\"></div>"
                + "  </body>" + "</html>";

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.requestFocusFromTouch();
        webView.getMeasuredWidth();
        webView.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "utf-8", null);

        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView challenge = (TextView) rootView.findViewById(R.id.challengeFriend);
                TextView addFriend = (TextView) rootView.findViewById(R.id.addFriend);
                TextView removeFriend = (TextView) rootView.findViewById(R.id.removeFriend);
                final LinearLayout toolbar = (LinearLayout) rootView.findViewById(R.id.friendToolbar);

                showToolBarAndRemoveFab(toolbar,fab);
                challenge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       showFabAndRemovetoolbar(toolbar,fab);
                        new Request(f, ((MainActivity)getActivity()).net).execute("FriendChallenge", friendID);
                    }
                });
                addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFabAndRemovetoolbar(toolbar, fab);
                        new Request(f, ((MainActivity)getActivity()).net).execute("FriendAdd", friendID);
                    }
                });
                removeFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFabAndRemovetoolbar(toolbar,fab);
                        new Request(f, ((MainActivity)getActivity()).net).execute("FriendRemove", friendID);
                    }
                });

            }
        });


        return rootView;
    }

    @Override
    public int getTitleResourceId() {
        return MainActivity.FIND_FRIEND;
    }
    public void addedFriend() {
        Toast.makeText(getActivity(), "Added friend", Toast.LENGTH_SHORT).show();
    }
    public void challengedFriend() {
        Toast.makeText(getActivity(), "Challenged friend", Toast.LENGTH_SHORT).show();
    }

    public void removedFriend() {
        Toast.makeText(getActivity(), "Removed friend", Toast.LENGTH_SHORT).show();
    }

    private void showToolBarAndRemoveFab(final View toolbar, final View fab) {
        int cx = (fab.getLeft() + fab.getRight()) / 2;
        int cy = (fab.getTop() + fab.getBottom()) / 2;
        int maxRadius =  Math.max(toolbar.getWidth(), toolbar.getHeight());
        int minRadius =  Math.max(fab.getWidth(), fab.getHeight());
        Animator revealAnimator = ViewAnimationUtils.createCircularReveal(toolbar,
                cx, cy, minRadius, maxRadius);

        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationEnd(animation);
                fab.setVisibility(View.INVISIBLE);

            }
        });
        toolbar.setVisibility(View.VISIBLE);
        revealAnimator.setDuration(500);
        revealAnimator.start();

    }
    private void showFabAndRemovetoolbar(final View toolbar, final View fab) {
        int cx = (fab.getLeft() + fab.getRight()) / 2;
        int cy = (fab.getTop() + fab.getBottom()) / 2;
        int maxRadius =  Math.max(toolbar.getWidth(), toolbar.getHeight());
        int minRadius =  Math.max(fab.getWidth(), fab.getHeight());
        Animator revealAnimator = ViewAnimationUtils.createCircularReveal(toolbar,
                cx, cy, maxRadius, minRadius);

        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fab.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.INVISIBLE);

            }
        });
        revealAnimator.setDuration(500);
        toolbar.setVisibility(View.VISIBLE);
        revealAnimator.start();


    }


}
