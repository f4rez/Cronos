package com.example.awesomeness.awesomeness;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;

import Json.Decode;
import Net.NetRequests;
import Net.Request;
import Question.Question;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    public NetRequests net;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
         net = new NetRequests("192.168.43.87:8080");
        setUpListeners();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void testlayout(String resp) {
        TextView t = (TextView) findViewById(R.id.jsonText);
        t.setText(resp);
    }

    public void setUpListeners() {
        Button b1 = (Button) findViewById(R.id.joinGame);
        Button b2 = (Button) findViewById(R.id.getQuestions);
        Button b3 = (Button) findViewById(R.id.answerQuestions);
        Button b4 = (Button) findViewById(R.id.regUser);
        Button b5 = (Button) findViewById(R.id.login);
        NumberPicker p = (NumberPicker) findViewById(R.id.pick);
        p.setMaxValue(100);
        p.setMinValue(0);


      b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);

    }

    public void setUpQuestions(String jsonString) {
        Decode d = new Decode();
        try {
            ArrayList<Question> questions = d.decodeQuestions(jsonString);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        String gameID;
        NumberPicker p;
        int ii = 0;
        ii = v.getId();
        Request r = new Request(this, net);

        switch (ii) {
            case R.id.joinGame:
                r.execute("JoinGame");
                break;
            case R.id.getQuestions:
                 p = (NumberPicker) findViewById(R.id.pick);
                 gameID = String.valueOf(p.getValue());
                Log.d("hehe","GameId:" + gameID);
                r.execute("GetQuestions", gameID);
                break;
            case R.id.answerQuestions:
                 p = (NumberPicker) findViewById(R.id.pick);
                 gameID = String.valueOf(p.getValue());
                r.execute("AnswerQuestions", gameID, "2","2","2","2","2");
                break;
            case R.id.regUser:
                r.execute("RegisterUser");
                break;
            case R.id.login:
                r.execute("Login");
                break;
        }
    }
}
