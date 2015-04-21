package com.example.awesomeness.awesomeness.Match;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.awesomeness.awesomeness.Adapters.DynamicListView;
import com.example.awesomeness.awesomeness.Adapters.MatchAdapter;
import com.example.awesomeness.awesomeness.Json.Decode;
import com.example.awesomeness.awesomeness.Net.GameRequests;
import com.example.awesomeness.awesomeness.Net.NetRequests;
import com.example.awesomeness.awesomeness.Question.Question;
import com.example.awesomeness.awesomeness.R;
import com.melnykov.fab.FloatingActionButton;



import java.util.ArrayList;


/**
 * Created by enigma on 2015-04-14.
 */
public class MatchActivity extends ActionBarActivity{

    public NetRequests net;
    public MatchAdapter mAdapter;
    public DynamicListView mListView;
    int gameID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        Bundle extras = getIntent().getExtras();
        gameID = extras.getInt("gameID");
        net = new NetRequests("192.168.43.87:8080",false);
        GameRequests req = new GameRequests(this,net);
        req.execute("GetQuestions", String.valueOf(gameID));

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

    public void checkAnswer() {
        String id = String.valueOf(gameID);
        switch(mAdapter.getCount()) {
            case 2:
               if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1))){
                   addNextQuestion();
               } else {
                   GameRequests r = new GameRequests(this,net);
                   r.execute("AnswerQuestions",id,"1","0","0","0","0");
               }
                break;
            case 3:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1), mAdapter.getItem(2))){
                    addNextQuestion();
                } else {
                    GameRequests r = new GameRequests(this,net);
                    r.execute("AnswerQuestions",id,"2","1","0","0","0");
                }
                break;
            case 4:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1), mAdapter.getItem(2), mAdapter.getItem(3))){
                    addNextQuestion();
                } else {
                    GameRequests r = new GameRequests(this,net);
                    r.execute("AnswerQuestions",id,"2","2","1","0","0");
                }
                break;
            case 5:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1), mAdapter.getItem(2), mAdapter.getItem(3),mAdapter.getItem(4))){
                    Log.d("MatchActivity","Allarätt");
                    GameRequests r = new GameRequests(this,net);
                    r.execute("AnswerQuestions",id,"2","2","2","2","2");
                } else {
                    Log.d("MatchActivity","inte alla rätt");
                    GameRequests r = new GameRequests(this,net);
                    r.execute("AnswerQuestions",id,"2","2","2","1","0");
                }
                break;
            case 6:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1), mAdapter.getItem(2), mAdapter.getItem(3),mAdapter.getItem(4),mAdapter.getItem(5))){
                    Log.d("MatchActivity","Allarätt");
                    GameRequests r = new GameRequests(this,net);
                    r.execute("AnswerQuestions",id,"2","2","2","2","2");
                } else {
                    Log.d("MatchActivity","inte alla rätt");
                    GameRequests r = new GameRequests(this,net);
                    r.execute("AnswerQuestions",id,"2","2","2","2","1");
                }
                break;
        }
    }


    public void addNextQuestion() {
           //Adapter.add(mAdapter.mQuestions.get(mAdapter.getCount()));
    }


    public boolean allCorrect(Question q1,Question q2) {
        return  q2.happendBefore(q1);
    }

    public boolean allCorrect(Question q1,Question q2, Question q3) {
        return  q2.happendBefore(q1) && q3.happendBefore(q2);
    }

    public boolean allCorrect(Question q1,Question q2, Question q3, Question q4) {
        return  q2.happendBefore(q1) && q3.happendBefore(q2) && q4.happendBefore(q3);
    }
    public boolean allCorrect(Question q1,Question q2, Question q3, Question q4, Question q5) {
        return  q2.happendBefore(q1) && q3.happendBefore(q2) && q4.happendBefore(q3) && q5.happendBefore(q4);
    }
    public boolean allCorrect(Question q1,Question q2, Question q3, Question q4, Question q5, Question q6) {
        return  q2.happendBefore(q1) && q3.happendBefore(q2) && q4.happendBefore(q3) && q5.happendBefore(q4) && q6.happendBefore(q5);
    }




    public void showQuestions(String json) {

        Decode d = new Decode();
        ArrayList<Question> list = d.decodeQuestions(json);
        if (mAdapter == null) {
            mAdapter = new MatchAdapter(this, R.layout.list_item, list);
        }
        if(mListView == null) {
            mListView = (DynamicListView) findViewById(R.id.questionList);
            mListView.setAdapter(mAdapter);
            mListView.setCheeseList(list);
            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(mListView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });

    }


}
