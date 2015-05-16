package se.zinister.timeline.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.view.CardView;
import se.zinister.timeline.Adapters.DynamicListView;
import se.zinister.timeline.Adapters.MatchAdapter;
import se.zinister.timeline.Items.Game;
import se.zinister.timeline.Json.Decode;
import se.zinister.timeline.MainActivity;
import se.zinister.timeline.Match.MatchActivity;
import se.zinister.timeline.Net.Request;
import se.zinister.timeline.Question.Question;
import se.zinister.timeline.Question.QuestionCard;
import se.zinister.timeline.R;


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
    private QuestionCard card;
    private FloatingActionButton fab;

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
        Question q = list.get(pos);
        mAdapter.addItem(q);
        mListView.addToList(0, list.get(pos));
        card.question = q.question;
        card.notifyDataSetChanged();
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
        fab.setImageResource(R.drawable.ic_action_back);
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
        if (mListView != null) {
            mListView.setAdapter(mAdapter);
            mListView.setCheeseList(tmp);
            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        }
        card = new QuestionCard(getActivity());

        //Set the card inner text
        card.question = tmp.get(0).question;
        //Set card in the cardView
        CardView cardView = (CardView) getActivity().findViewById(R.id.card);
        cardView.setCard(card);
        cardView.setVisibility(View.VISIBLE);



        fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.attachToListView(mListView);
        fab.setImageResource(R.drawable.ic_action_accept);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasSeenYears) {
                    int points = checkAnswer();
                    if (points > -1) {
                        MatchActivity m = (MatchActivity) getActivity();

                        Game.Round r = m.game.rounds.get(m.game.rounds.size() - 1);
                        if (r.oppRoundScore == -1) {
                            r.myRoundScore = points;
                            m.game.turn = !m.game.turn;
                        } else {
                            if (m.game.rounds.size() >=5) {
                                r.myRoundScore = points;
                            } else {
                                Game.Round round = new Game.Round();
                                r.myRoundScore = points;
                                m.game.addRound(round);
                            }
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
