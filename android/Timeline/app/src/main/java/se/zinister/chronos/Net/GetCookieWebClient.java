package se.zinister.chronos.Net;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by josef on 2015-05-12.
 *
 */
public class GetCookieWebClient extends WebViewClient {
    @Override
    public void onPageFinished(WebView view, String url){
        view.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
    }
}




