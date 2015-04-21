package com.example.awesomeness.awesomeness.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import com.example.awesomeness.awesomeness.Adapters.DynamicListView;
import com.example.awesomeness.awesomeness.Adapters.MatchAdapter;
import com.example.awesomeness.awesomeness.Json.Decode;
import com.example.awesomeness.awesomeness.MainActivity;
import com.example.awesomeness.awesomeness.Match.MatchActivity;
import com.example.awesomeness.awesomeness.Net.Request;
import com.example.awesomeness.awesomeness.Question.Question;
import com.example.awesomeness.awesomeness.R;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

/**
 * Created by josef on 2015-04-21.
 */
public class MatchFragment extends BaseFragment {

    public MatchAdapter mAdapter;
    public DynamicListView mListView;
    private MatchActivity matchActivity;
    public static int gameID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.match_fragment, container, false);
        matchActivity = (MatchActivity)getActivity();
        Request req = new Request(this,matchActivity.net);
        req.execute("GetQuestions", String.valueOf(matchActivity.gameID));

        return rootView;
    }



    @Override
    public int getTitleResourceId() {
        return matchActivity.MATCHPAGE;
    }


    public void checkAnswer() {
        String id = String.valueOf(gameID);
        switch(mAdapter.getCount()) {
            case 2:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1))){
                    addNextQuestion();
                } else {
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"1","0","0","0","0");
                }
                break;
            case 3:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1), mAdapter.getItem(2))){
                    addNextQuestion();
                } else {
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"2","1","0","0","0");
                }
                break;
            case 4:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1), mAdapter.getItem(2), mAdapter.getItem(3))){
                    addNextQuestion();
                } else {
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"2","2","1","0","0");
                }
                break;
            case 5:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1), mAdapter.getItem(2), mAdapter.getItem(3),mAdapter.getItem(4))){
                    Log.d("MatchActivity", "Allarätt");
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"2","2","2","2","2");
                } else {
                    Log.d("MatchActivity","inte alla rätt");
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"2","2","2","1","0");
                }
                break;
            case 6:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1), mAdapter.getItem(2), mAdapter.getItem(3),mAdapter.getItem(4),mAdapter.getItem(5))){
                    Log.d("MatchActivity","Allarätt");
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"2","2","2","2","2");
                } else {
                    Log.d("MatchActivity","inte alla rätt");
                    Request r = new Request(this,matchActivity.net);
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
            mAdapter = new MatchAdapter(matchActivity, R.layout.list_item, list);
        }
        if(mListView == null) {
            mListView = (DynamicListView) getView().findViewById(R.id.questionList);
            mListView.setAdapter(mAdapter);
            mListView.setCheeseList(list);
            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.attachToListView(mListView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });

    }
}
