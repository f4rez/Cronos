package com.example.awesomeness.awesomeness.Net;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.util.Log;

import com.example.awesomeness.awesomeness.MainActivity;
import com.example.awesomeness.awesomeness.Match.MatchActivity;
import com.example.awesomeness.awesomeness.fragments.ChallengeFriendFragment;
import com.example.awesomeness.awesomeness.fragments.FindUsersFragment;
import com.example.awesomeness.awesomeness.fragments.MainPageFragment;
import com.example.awesomeness.awesomeness.fragments.MatchFragment;

import java.util.Objects;

/**
 * Created by josef on 2015-04-13.
 */
public class Request extends AsyncTask<String, Void, String> {

    private final Fragment caller;
    NetRequests net;
    String action;

    public Request(Fragment caller, NetRequests net) {
        this.caller = caller;
        this.net = net;
    }


    @Override
    protected String doInBackground(String... string) {
        action = string[0];
        switch (action){
            case "GetQuestions":
                String id = string[1];
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
            case "FriendList":
                return net.friendList();
            case "Search":
                return net.search(string[1], string[2]);
            case "FriendAdd":
                return net.friend("add", string[1]);
            case "FriendChallenge":
                return net.friend("challenge", string[1]);
        }
        return "error";
    }

    @Override
    protected void onPostExecute(String returned) {
        switch (action){
            case "JoinGame":
                MainPageFragment m2 = (MainPageFragment) caller;
                m2.showMatches(returned);
                break;
            case "Login":
                MainPageFragment mainActivity = (MainPageFragment) caller;
                mainActivity.doneLogin();
                break;
            case "RegisterUser":
                MainPageFragment m1 = (MainPageFragment) caller;
                m1.doneRegister();
                break;
            case "GetQuestions":
                MatchFragment matchFragment = (MatchFragment) caller;
                matchFragment.showQuestions(returned);
                break;
            case "FriendList":
                ChallengeFriendFragment c = (ChallengeFriendFragment) caller;
                c.showFriends(returned);
                break;
            case "Search":
                FindUsersFragment f = (FindUsersFragment) caller;
                f.showResult(returned);

            case "FriendAdd":
                FindUsersFragment f2 = (FindUsersFragment) caller;
                f2.addedFriend();
            case "FriendChallenge":
                ChallengeFriendFragment f1 = (ChallengeFriendFragment) caller;
                f1.challengedFriend();
        }

    }




}
