package com.example.awesomeness.awesomeness;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;

import com.example.awesomeness.awesomeness.Adapters.MatchAdapter;
import com.example.awesomeness.awesomeness.Adapters.StartPageAdapter;
import com.example.awesomeness.awesomeness.Json.Decode;
import com.example.awesomeness.awesomeness.Match.GamesOverview;
import com.example.awesomeness.awesomeness.Match.MatchActivity;
import com.example.awesomeness.awesomeness.Net.NetRequests;
import com.example.awesomeness.awesomeness.Net.Request;
import com.example.awesomeness.awesomeness.Question.Question;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    public NetRequests net;
    public StartPageAdapter mAdapter;
    public ListView mListview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        net = new NetRequests("192.168.0.22:8080");
        setUpListeners();
        Request r = new Request(this,net);
        r.execute("Login");
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




    public void setUpListeners() {
        Button b1 = (Button) findViewById(R.id.joinGame);
        Button b2 = (Button) findViewById(R.id.regUser);
        Button b3 = (Button) findViewById(R.id.login);


        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);

    }



    public void showMatches(String jsonString) {
        Log.d("mainActivity"," ENterd showMatches json = " + jsonString);
        Decode d = new Decode();
        ArrayList<GamesOverview> gamesOverviews = d.decodeGamesOverview(jsonString);
        if (mAdapter == null){
            mAdapter = new StartPageAdapter(this,R.layout.game_overview,gamesOverviews);
        } else {
                mAdapter.clear();
                mAdapter.addAll(gamesOverviews);
        }


        if (mListview == null)
            mListview = (ListView) findViewById(R.id.mMatches);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GamesOverview g = (GamesOverview) parent.getItemAtPosition(position);
                if(g.myTurn) {
                    StartPageAdapter s = (StartPageAdapter) parent.getAdapter();
                    Intent n = new Intent(s.c, MatchActivity.class);
                    n.putExtra("gameID", g.gameID);
                    s.c.startActivity(n);
                }
            }
        });
        mListview.setAdapter(mAdapter);

    }

    public void doneLogin(String j) {
        Request r = new Request(this, net);
        r.execute("RegisterUser");
    }

    public void doneRegister(String j) {
        Request r = new Request(this, net);
        r.execute("StartMessage");
    }
    public  void doneJoining(String j) {
        Request r = new Request(this, net);
        r.execute("StartMessage");
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
            case R.id.regUser:
                r.execute("RegisterUser");
                break;
            case R.id.login:
                r.execute("Login");
                break;
        }
    }
}
