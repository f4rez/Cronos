package se.zinister.timeline.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import se.zinister.timeline.MainActivity;
import se.zinister.timeline.R;


/**
 * Created by josef on 2015-04-23.
 */
public class FriendFragment extends  BaseFragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.friend_fragment, container, false);
        Bundle extras = getActivity().getIntent().getExtras();
        byte[] b = extras.getByteArray("FriendPicture");
        String name = extras.getString("FriendName");
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
                + "        chart.draw(data);"
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

        return rootView;
    }

    @Override
    public int getTitleResourceId() {
        return ((MainActivity)getActivity()).FIND_FRIEND;
    }
}
