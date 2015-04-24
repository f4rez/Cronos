package com.example.awesomeness.awesomeness.Json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.example.awesomeness.awesomeness.Items.Friend;
import com.example.awesomeness.awesomeness.Items.Game;
import com.example.awesomeness.awesomeness.MainActivity;
import com.example.awesomeness.awesomeness.Match.GamesOverview;
import com.example.awesomeness.awesomeness.Question.Question;

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


    public ArrayList<GamesOverview> decodeGamesOverview(String in) {
        ArrayList<GamesOverview> list = new ArrayList<>();
        if (in == "error") return list;
        JSONArray json = null;
        try {
            json = new JSONArray(in);
            for (int i = 0; i < json.length(); i++) {
                JSONObject object = (JSONObject) json.get(i);
                GamesOverview q = new GamesOverview(object.getInt("GID"),object.getInt("MPoints"),
                        object.getInt("OPoints"),object.getInt("Turn"),object.getString("OppName"),
                        object.getString("OppID"), object.getBoolean("MyTurn"));
                list.add(q);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
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
