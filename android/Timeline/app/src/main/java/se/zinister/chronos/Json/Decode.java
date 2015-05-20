package se.zinister.chronos.Json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import se.zinister.chronos.Items.Challenge;
import se.zinister.chronos.Items.Friend;
import se.zinister.chronos.Items.Game;
import se.zinister.chronos.Items.Profile;
import se.zinister.chronos.Items.StartpageMessage;
import se.zinister.chronos.Items.User;
import se.zinister.chronos.MainActivity;
import se.zinister.chronos.Match.GamesOverview;
import se.zinister.chronos.Question.Question;


/**
 * Created by josef on 2015-04-13.
 *
 */
public class Decode {

    public ArrayList<Question> decodeQuestions(String in) {
        ArrayList<Question> list = new ArrayList<>();
        if (in.equals("error")) return list;
        JSONArray json;
        Log.d(MainActivity.TAG,"string: " + in);
        try {
            json = new JSONArray(in);
            for (int i = 0; i < json.length(); i++) {
                JSONObject object = (JSONObject) json.get(i);
                Question q = new Question(object.getString("Question"), object.getInt("Level"), object.getInt("ID"), object.getInt("Year"));
                list.add(q);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    public StartpageMessage decodeGamesOverview(String in) {
        StartpageMessage startpageMessage = new StartpageMessage();
        if (in.equals("error")) return startpageMessage;
        JSONObject json;
        ArrayList<GamesOverview> gamesOverviews = new ArrayList<>();
        ArrayList<Challenge> challenges = new ArrayList<>();
        try {
            json = new JSONObject(in);
            JSONArray games = json.getJSONArray("Games");
            for (int i = 0; i < games.length(); i++) {
                JSONObject object = (JSONObject) games.get(i);
                GamesOverview q = new GamesOverview(object.getInt("GID"),object.getInt("MPoints"),
                        object.getInt("OPoints"),object.getInt("Turn"),object.getString("OppName"),
                        object.getString("OppID"), object.getString("OppPic"), object.getBoolean("MyTurn"));
                gamesOverviews.add(q);
            }
            JSONArray finished = json.getJSONArray("Finished");
            for (int i = 0; i < finished.length(); i++) {
                JSONObject object = (JSONObject) finished.get(i);
                GamesOverview q = new GamesOverview(4);
                q.myPoint = object.getInt("MyScore");
                q.gameID = object.getInt("GID");
                q.opponentPoint = object.getInt("OppScore");
                q.opponentName = object.getString("OppName");
                q.opponentPic = object.getString("OppPic");
                gamesOverviews.add(q);
            }
            startpageMessage.games = gamesOverviews;
            JSONArray challenge = json.getJSONArray("Challenges");
            Log.d(MainActivity.TAG,"Challenges: " + challenge.length());
            for (int i = 0; i < challenge.length(); i++) {
                Log.d(MainActivity.TAG,"Challenges: " + challenge.length());
                JSONObject object = (JSONObject) challenge.get(i);
                Challenge q = new Challenge(object.getString("OppID"),object.getString("OppName"));
                challenges.add(q);
            }
            startpageMessage.challenges = challenges;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return startpageMessage;
    }


    public ArrayList <Friend> decodeFriendList(String in) {
        ArrayList<Friend> list = new ArrayList<>();
        if (in.equals("error")) return list;
        JSONArray json;
        if (MainActivity.DEBUG)Log.d(MainActivity.TAG,in);
        try {
            json = new JSONArray(in);
            for (int i = 0; i < json.length(); i++) {
                JSONObject object = (JSONObject) json.get(i);
                Friend q = new Friend(object.getString("Name"),object.getString("Oid"), object.getString("Picture"),
                        object.getInt("Won"),object.getInt("Draw"), object.getInt("Lost"), true);
                list.add(q);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    public Profile decodeProfile(String in) {
        if (in.equals("error")) return null;
        JSONArray json;
        Profile p = null;
        if (MainActivity.DEBUG)Log.d(MainActivity.TAG,in);
        try {
            ArrayList<Friend> list = new ArrayList<>();
            JSONObject o = new JSONObject(in);
            json = o.getJSONArray("Friends");
            for (int i = 0; i < json.length(); i++) {
                JSONObject object = (JSONObject) json.get(i);
                Friend q = new Friend(object.getString("Name"),object.getString("Oid"), object.getString("Picture"),
                        object.getInt("Won"),object.getInt("Draw"), object.getInt("Lost"), true);
                list.add(q);
            }
            p = new Profile(list,o.getInt("Won"), o.getInt("Lost"), o.getInt("Draw"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return p;
    }


    public ArrayList <Friend> decodeSearchList(String in) {
        ArrayList<Friend> list = new ArrayList<>();
        if (in.equals("error")) return list;
        JSONArray json;
        if (MainActivity.DEBUG)Log.d(MainActivity.TAG,in);
        try {
            json = new JSONArray(in);
            for (int i = 0; i < json.length(); i++) {
                JSONObject o = (JSONObject) json.get(i);
                JSONObject object = o.getJSONObject("Usr");
                Friend q = new Friend(object.getString("Name"),object.getString("Oid"), object.getString("Picture"),
                        object.getInt("Won"),object.getInt("Draw"), object.getInt("Lost"), o.getBoolean("IsFriend"));
                list.add(q);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Game decodeGame(String in) {
        Game r = null;
        if (in.equals("error")) return null;
        if (MainActivity.DEBUG) Log.d(MainActivity.TAG, in);
        JSONObject json;
         try {
            json = new JSONObject(in);
             if (MainActivity.DEBUG) Log.d(MainActivity.TAG, json.getString("FName"));
             String fName = json.getString("FName");
             r = new Game(json.getString("FID"),fName, json.getString("FPic"),
                    json.getString("SID"), json.getString("SName"), json.getString("SPic"),
                     json.getInt("NumberOfTurns"),json.getBoolean("Turn"));

             JSONArray j = json.getJSONArray("Rounds");
             boolean first = false;
             if (fName.equals(MainActivity.MY_NAME)) first = true;
             for(int i = 0; i< j.length();i++) {
                 JSONObject obj = j.getJSONObject(i);
                 Game.Round round = new Game.Round();
                 if(first) {
                     round.myRoundScore = obj.getInt("PlayerOnePoints");
                     round.oppRoundScore = obj.getInt("PlayerTwoPoints");
                 } else {
                     round.myRoundScore = obj.getInt("PlayerTwoPoints");
                     round.oppRoundScore = obj.getInt("PlayerOnePoints");
                 }
                 r.addRound(round);
             }
        } catch (JSONException e) {
             e.printStackTrace();
         }



        return r;
    }

    public User decodeUser(String in) {
        User u = null;
        try {
            JSONObject json = new JSONObject(in);
            u = new User(json.getString("Oid"), json.getString("Name"), json.getString("Picture"), json.getString("Token"), json.getInt("Won"), json.getInt("Draw"), json.getInt("Lost"), json.getInt("Level"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }
}
