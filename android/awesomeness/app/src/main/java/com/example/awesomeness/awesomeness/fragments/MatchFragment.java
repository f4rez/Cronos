package com.example.awesomeness.awesomeness.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import com.example.awesomeness.awesomeness.Adapters.DynamicListView;
import com.example.awesomeness.awesomeness.Adapters.MatchAdapter;
import com.example.awesomeness.awesomeness.Items.Game;
import com.example.awesomeness.awesomeness.Json.Decode;
import com.example.awesomeness.awesomeness.MainActivity;
import com.example.awesomeness.awesomeness.Match.MatchActivity;
import com.example.awesomeness.awesomeness.Net.Request;
import com.example.awesomeness.awesomeness.Question.Question;
import com.example.awesomeness.awesomeness.R;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardView;
import it.gmariotti.cardslib.library.view.CardViewNative;

/**
 * Created by josef on 2015-04-21.
 */
public class MatchFragment extends BaseFragment {

    public MatchAdapter mAdapter;
    public DynamicListView mListView;
    private MatchActivity matchActivity;
    public static int gameID;
    private ArrayList<Question> list;
    private Boolean hasSeenYears = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.match_fragment, container, false);
        matchActivity = (MatchActivity)getActivity();
        gameID = matchActivity.gameID;
        Request req = new Request(this,matchActivity.net);
        req.execute("GetQuestions", String.valueOf(matchActivity.gameID));
        return rootView;
    }



    @Override
    public int getTitleResourceId() {
        return matchActivity.MATCHPAGE;
    }


    public int checkAnswer() {
        String id = String.valueOf(gameID);
        switch(mAdapter.getCount()) {
            case 2:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1))){
                    addNextQuestion(2);
                } else {
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"1","0","0","0","0");
                    return 0;
                }
                break;
            case 3:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1), mAdapter.getItem(2))){
                    addNextQuestion(3);
                } else {
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"2","1","0","0","0");
                    return 1;
                }
                break;
            case 4:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1), mAdapter.getItem(2), mAdapter.getItem(3))){
                    addNextQuestion(4);
                } else {
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"2","2","1","0","0");
                    return 2;
                }
                break;
            case 5:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1), mAdapter.getItem(2), mAdapter.getItem(3),mAdapter.getItem(4))){
                    addNextQuestion(5);
                } else {
                    Log.d("MatchActivity","inte alla rätt");
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"2","2","2","1","0");
                    return 3;
                }
                break;
            case 6:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1), mAdapter.getItem(2), mAdapter.getItem(3),mAdapter.getItem(4),mAdapter.getItem(5))){
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"2","2","2","2","2");
                    return 6;
                } else {
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"2","2","2","2","1");
                    return 5;
                }

        }
        return -1;
    }


    public void addNextQuestion(int pos) {
        if (MainActivity.DEBUG) Log.d(MainActivity.TAG, "Add new question: " + pos);
        mAdapter.addItem(list.get(pos));
        mListView.addToList(0,list.get(pos));
        mAdapter.notifyDataSetChanged();
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


    public void showYears() {
        for (int i = 0; i < mListView.getCount(); i++){
            mListView.getViewForID(mAdapter.getItemId(i)).findViewById(R.id.year).setVisibility(View.VISIBLE);
        }
        hasSeenYears = true;
    }



    public void showQuestions(String json) {

        Decode d = new Decode();
        list = d.decodeQuestions(json);
        ArrayList <Question> tmp = new ArrayList<>();
        tmp.add(list.get(0));
        tmp.add(list.get(1));


        mAdapter = new MatchAdapter(matchActivity, R.layout.list_item, tmp);


        mListView = (DynamicListView) getView().findViewById(R.id.questionList);
        mListView.setAdapter(mAdapter);
        mListView.setCheeseList(tmp);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        Card card = new Card(getActivity());

        //Set the card inner text
        card.setTitle("My Title");


        //Set card in the cardView
        CardViewNative cardView = (CardViewNative) getActivity().findViewById(R.id.card);
        cardView.setCard(card);



        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.attachToListView(mListView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasSeenYears) {
                    int points = checkAnswer();
                    if (points > 0) {
                        MatchActivity m = (MatchActivity) getActivity();

                        Game.Round r = m.game.rounds.get(m.game.rounds.size() - 1);
                        if (r.oppRoundScore == -1) {
                            r.myRoundScore = points;
                            m.game.turn = !m.game.turn;
                        } else {
                            Game.Round round = new Game.Round();
                            round.myRoundScore = points;
                            m.game.addRound(round);

                        }
                        showYears();
                    }
                }
                else {
                    MatchActivity m = (MatchActivity) getActivity();
                    m.changeFragment(MatchActivity.STATISTICS);
                }
            }
        });

    }
}
