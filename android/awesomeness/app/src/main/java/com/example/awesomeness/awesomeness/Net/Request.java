package com.example.awesomeness.awesomeness.Net;

import android.app.Fragment;
import android.os.AsyncTask;

import com.example.awesomeness.awesomeness.fragments.ChallengeFriendFragment;
import com.example.awesomeness.awesomeness.fragments.FindUsersFragment;
import com.example.awesomeness.awesomeness.fragments.StartPageFragment;
import com.example.awesomeness.awesomeness.fragments.MatchFragment;
import com.example.awesomeness.awesomeness.fragments.MatchStatistics;

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
            case "GetGame":
                return net.game(string[1]);
        }
        return "error";
    }

    @Override
    protected void onPostExecute(String returned) {
        switch (action){
            case "JoinGame":
                StartPageFragment m2 = (StartPageFragment) caller;
                m2.showMatches(returned);
                break;
            case "Login":
                StartPageFragment mainActivity = (StartPageFragment) caller;
                mainActivity.doneLogin();
                break;
            case "RegisterUser":
                StartPageFragment m1 = (StartPageFragment) caller;
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
                break;
            case "FriendAdd":
                FindUsersFragment f2 = (FindUsersFragment) caller;
                f2.addedFriend();
                break;
            case "FriendChallenge":
                ChallengeFriendFragment f1 = (ChallengeFriendFragment) caller;
                f1.challengedFriend();
                break;
            case "StartMessage":
                StartPageFragment pageFragment = (StartPageFragment) caller;
                pageFragment.showMatches(returned);
                break;
            case "GetGame":
                MatchStatistics matchStatistics = (MatchStatistics) caller;
                matchStatistics.saveGame(returned);
                break;
        }

    }




}
