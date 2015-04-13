package Net;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by josef on 2015-04-06.
 */
public class NetRequests {
    String host;
    HttpURLConnection urlConnection;
    URL url;


    public NetRequests(String h) {
        host = h;
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        HttpCookie cookie = new HttpCookie("dev_appserver_login", "h@gmail.com:false:130142017656123235177");
        //HttpCookie cookie = new HttpCookie("dev_appserver_login", "g@gmail.com:false:130142017656123235188");
        cookie.setDomain(host);
        cookie.setPath("/");
        cookie.setVersion(0);
        try {
            cookieManager.getCookieStore().add(new URI(host), cookie);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public String joinGame() {
        try {
            url = new URL(host + "Http://www."+ host +"/joinGame");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally{
                urlConnection.disconnect();
            }
       return "";
        }

    public String getQuestions(int gameID) {
        URL url;
        try {
            url = new URL(host + "Http://www."+ host +"/match?action=getQuestions&gameID=" + gameID);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            urlConnection.disconnect();
        }
        return "";
    }


    public String answerQuestions(int gameID, String a1,String a2, String a3, String a4, String a5){
        URL url;
        try {
            url = new URL(host + "Http://www."+ host +"/match?action=answerQuestions=" + gameID + "&a1=" + a1 +
                    "&a2=" + a2 + "&a3=" + a3 + "&a4=" + a4 + "&a5=" + a5);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            urlConnection.disconnect();
        }
        return "";
    }


    public String registerUSer() {

        try {
            url = new URL(host +"/registerNewUser");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            urlConnection.disconnect();
        }
        return "";

    }


 }