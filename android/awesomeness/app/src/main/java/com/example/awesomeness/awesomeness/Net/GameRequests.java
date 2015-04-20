package com.example.awesomeness.awesomeness.Net;

import android.os.AsyncTask;

import com.example.awesomeness.awesomeness.Match.MatchActivity;

/**
 * Created by Josef on 2015-04-20.
 */
public class GameRequests extends AsyncTask<String, Void, String> {

    private final MatchActivity caller;
    NetRequests net;
    String action;

    public GameRequests(MatchActivity caller, NetRequests net) {
        this.caller = caller;
        this.net = net;
    }


    @Override
    protected String doInBackground(String... string) {
        action = string[0];
        switch (action) {
            case "GetQuestions":
                String id = string[1];
                return net.getQuestions(id);
            case "AnswerQuestions":
                return net.answerQuestions(Integer.parseInt(string[1]), string[2], string[3], string[4], string[5], string[6]);
        }
        return "error";
    }

    @Override
    protected void onPostExecute(String returned) {
        switch (action) {
            case "GetQuestions":
                caller.showQuestions(returned);
                break;
        }

    }

}
