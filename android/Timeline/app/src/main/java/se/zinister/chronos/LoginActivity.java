package se.zinister.chronos;



import android.app.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;

import se.zinister.chronos.Net.GetCookieWebClient;


public class LoginActivity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        WebView v = (WebView)findViewById(R.id.webview);
        WebSettings webSettings = v.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        v.setWebViewClient(new GetCookieWebClient());
        v.loadUrl("https://calcium-firefly-93808.appspot.com");

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }





}
