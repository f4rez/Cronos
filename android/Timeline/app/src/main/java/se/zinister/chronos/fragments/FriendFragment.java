package se.zinister.chronos.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
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
        View rootView = inflater.inflate(R.layout.friend_fragment, container, false);
        Bundle extras = getActivity().getIntent().getExtras();
        byte[] b = extras.getByteArray("FriendPicture");
        String name = extras.getString("FriendName");
        friendID = extras.getString("FriendID");
        int won = extras.getInt("won");
        int draw = extras.getInt("draw");
        int lost = extras.getInt("lost");
        final boolean isFriend = extras.getBoolean("isFriend");

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

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.DEBUG) Log.d(MainActivity.TAG,"isFriend = "+ isFriend);
                if(isFriend) {
                    new Request(f, ((MainActivity) getActivity()).net).execute("FriendChallenge", friendID);
                } else {
                    new Request(f, ((MainActivity) getActivity()).net).execute("FriendAdd", friendID);
                }
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

}
