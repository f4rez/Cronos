package se.zinister.chronos.Net;

import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by josef on 2015-05-12.
 */
public class GetCookieWebClient extends WebViewClient {
    @Override
    public void onPageFinished(WebView view, String url){
        String cookies = CookieManager.getInstance().getCookie(url);
        Log.d("COKIES", "All the cookies in a string:" + cookies);


    }


}
