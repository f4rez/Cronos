package Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Question.Question;

/**
 * Created by josef on 2015-04-13.
 */
public class Decode {



    public ArrayList<Question> decodeQuestions(String in) throws JSONException {
        ArrayList<Question> list = new ArrayList<>();
        JSONArray json = new JSONArray(in);
        for (int i = 0; i < json.length(); i++) {
            JSONObject object = (JSONObject) json.get(i);
            Question q = new Question(object.getString("Question"),object.getInt("Level"),object.getInt("ID"),object.getInt("Year"));
            list.add(q);
        }
        return list;
    }
}
