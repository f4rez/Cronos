package se.zinister.chronos.Net;

import android.app.Fragment;
import android.os.AsyncTask;

import se.zinister.chronos.MainActivity;
import se.zinister.chronos.fragments.ChallengeFriendFragment;
import se.zinister.chronos.fragments.FindUsersFragment;
import se.zinister.chronos.fragments.FriendFragment;
import se.zinister.chronos.fragments.MatchFragment;
import se.zinister.chronos.fragments.MatchStatistics;
import se.zinister.chronos.fragments.StartPageFragment;


/**
 * Created by josef on 2015-04-13.
 */
public class Request extends AsyncTask<String, Void, String> {

    private Fragment caller = null;
    private MainActivity c = null;
    NetRequests net;
    String action;

    public Request(Fragment caller, NetRequests net) {
        this.caller = caller;
        this.net = net;
    }

    public  Request(MainActivity caller, NetRequests net) {
        this.c = caller;
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
                return net.registerUser(string[1], string[2]);
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
            case "AnswerChallange":
                return net.answerChallange(string[1],string[2]);
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
                mainActivity.doneLogin(returned);
                break;
            case "RegisterUser":
                MainActivity m1 = c;
                m1.doneRegister(returned);
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
                FriendFragment f2 = (FriendFragment) caller;
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
            case "AnswerChallenge":
                StartPageFragment s = (StartPageFragment)caller;
                s.challengeReg();
        }

    }




}
