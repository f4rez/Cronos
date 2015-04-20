package com.example.awesomeness.awesomeness.Net;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
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
    }
    public NetRequests(String h, boolean b) {
        host=h;
    }

    public String login() {
        try {
            url = new URL("http://"+ host + "/_ah/login?email=philip%40example.com&action=Login&continue=http://"+host);
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
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return "felf elfelfel";
    }

    public String joinGame() {
        try {
            url = new URL("http://" + host + "/joinGame");
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
                if (urlConnection != null)
                urlConnection.disconnect();
            }
       return "felf elfelfel";
        }

    public String getQuestions(String gameID) {
        URL url;
        try {
            url = new URL("http://"+ host +"/match?action=getQuestions&game_id=" + gameID);
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
            url = new URL( "http://" + host +"/match?action=answerQuestions&game_id=" + gameID + "&a1=" + a1 +
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
            url = new URL("http://" +host +"/registerNewUser");
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

    public String startPage() {

        try {
            url = new URL("http://" +host +"/startMess");
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
            Log.d("hehe","ssss");
            e.printStackTrace();
        } finally{
            urlConnection.disconnect();
        }
        Log.d("hehe","slut");
        return "";

    }


 }