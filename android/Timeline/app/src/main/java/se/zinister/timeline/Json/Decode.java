package se.zinister.timeline.Json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import se.zinister.timeline.Items.Challenge;
import se.zinister.timeline.Items.Friend;
import se.zinister.timeline.Items.Game;
import se.zinister.timeline.Items.StartpageMessage;
import se.zinister.timeline.MainActivity;
import se.zinister.timeline.Match.GamesOverview;
import se.zinister.timeline.Question.Question;


/**
 * Created by josef on 2015-04-13.
 */
public class Decode {

    public ArrayList<Question> decodeQuestions(String in) {
        ArrayList<Question> list = new ArrayList<>();
        if (in == "error") return list;
        JSONArray json = null;
        Log.d("Decode","string: " + in);
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
        if (in == "error") return startpageMessage;
        JSONObject json = null;
        ArrayList<GamesOverview> gamesOverviews = new ArrayList<>();
        ArrayList<Challenge> challenges = new ArrayList<>();
        try {
            json = new JSONObject(in);
            JSONArray games = json.getJSONArray("Games");
            for (int i = 0; i < games.length(); i++) {
                JSONObject object = (JSONObject) games.get(i);
                GamesOverview q = new GamesOverview(object.getInt("GID"),object.getInt("MPoints"),
                        object.getInt("OPoints"),object.getInt("Turn"),object.getString("OppName"),
                        object.getString("OppID"), object.getBoolean("MyTurn"));
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
                gamesOverviews.add(q);
            }
            startpageMessage.games = gamesOverviews;
            JSONArray challenge = json.getJSONArray("Challenges");
            for (int i = 0; i < challenge.length(); i++) {
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
        if (in == "error") return list;
        JSONArray json = null;
        try {
            json = new JSONArray(in);
            for (int i = 0; i < json.length(); i++) {
                JSONObject object = (JSONObject) json.get(i);
                Friend q = new Friend(object.getString("Name"),object.getString("Oid"),"");
                list.add(q);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    public Game decodeGame(String in) {
        Game r = null;
        if (in == "error") return r;
        if (MainActivity.DEBUG) Log.d(MainActivity.TAG, in);
        JSONObject json = null;
         try {
            json = new JSONObject(in);
             if (MainActivity.DEBUG) Log.d(MainActivity.TAG, json.getString("FName"));
             r = new Game(json.getString("FID"),json.getString("FName"), json.getString("FPic"),
                    json.getString("SID"), json.getString("SName"), json.getString("SPic"),json.getInt("NumberOfTurns"),json.getBoolean("Turn"));

             JSONArray j = json.getJSONArray("Rounds");
             for(int i = 0; i< j.length();i++) {
                 JSONObject obj = j.getJSONObject(i);
                 Game.Round round = new Game.Round();
                 round.myRoundScore = obj.getInt("PlayerOnePoints");
                 round.oppRoundScore = obj.getInt("PlayerTwoPoints");
                 r.addRound(round);
             }
        } catch (JSONException e) {
             e.printStackTrace();
         }



        return r;
    }
}
