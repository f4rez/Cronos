package com.example.awesomeness.awesomeness.Json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.example.awesomeness.awesomeness.Match.GamesOverview;
import com.example.awesomeness.awesomeness.Question.Question;

/**
 * Created by josef on 2015-04-13.
 */
public class Decode {



    public ArrayList<Question> decodeQuestions(String in) {
        ArrayList<Question> list = new ArrayList<>();
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
}
