package com.example.awesomeness.awesomeness.Net;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.awesomeness.awesomeness.MainActivity;
import com.example.awesomeness.awesomeness.Match.MatchActivity;

/**
 * Created by josef on 2015-04-13.
 */
public class Request extends AsyncTask<String, Void, String> {

    private final Activity caller;
    NetRequests net;
    String action;

    public Request(Activity caller, NetRequests net) {
        this.caller = caller;
        this.net = net;
    }


    @Override
    protected String doInBackground(String... string) {
        action = string[0];
        switch (action){
         case "GetQuestions":
             String s = string[1];
             int id = Integer.parseInt(s);
             return net.getQuestions(id);
         case "JoinGame":
             return net.joinGame();
         case "AnswerQuestions":
             return net.answerQuestions(Integer.parseInt(string[1]),string[2],string[3],string[4],string[5],string[6]);
         case "RegisterUser":
             return net.registerUSer();
         case "Login":
             return net.login();
            case "StartMessage":
             return net.startPage();
        }
        return "error";
    }

    @Override
    protected void onPostExecute(String returned) {
        switch (action){

            case "GetQuestions":
                MatchActivity m1 = (MatchActivity) caller;
                m1.showQuestions(returned);
                break;
            case "JoinGame":
                MainActivity m2 = (MainActivity) caller;
                m2.doneJoining(returned);
                break;
            case "AnswerQuestions":
                MatchActivity m3 = (MatchActivity) caller;
                m3.showQuestions(returned);
                break;
            case "RegisterUser":
                MainActivity m4 = (MainActivity) caller;
                m4.doneRegister(returned);
                break;
            case "Login":
                MainActivity m5 = (MainActivity) caller;
                m5.doneLogin(returned);
                break;
            case "StartMessage":
                MainActivity m6 = (MainActivity) caller;
                m6.showMatches(returned);
        }

    }




}
