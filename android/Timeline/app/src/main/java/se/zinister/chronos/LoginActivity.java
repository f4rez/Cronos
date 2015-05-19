package se.zinister.chronos;



import android.annotation.SuppressLint;
import android.app.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import se.zinister.chronos.Net.GetCookieWebClient;


public class LoginActivity extends Activity {


    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_login);
        WebView v = (WebView)findViewById(R.id.webview);
        WebSettings webSettings = v.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        v.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        v.setWebViewClient(new GetCookieWebClient());
        v.loadUrl(extras.getString("url"));


    }



    class MyJavaScriptInterface {
        @JavascriptInterface
        public void showHTML(final String html) {
            if (html.substring(25,35).equals("logOutUrl:")) {
                MainActivity.LOGOUT = html.substring(35,html.indexOf("</body>"));
                setResult(Activity.RESULT_OK);
                finish();
            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }





}
