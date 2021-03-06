package se.zinister.chronos.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import it.gmariotti.cardslib.library.view.CardView;
import se.zinister.chronos.Adapters.DynamicListView;
import se.zinister.chronos.Adapters.MatchAdapter;
import se.zinister.chronos.Items.Game;
import se.zinister.chronos.Json.Decode;
import se.zinister.chronos.MainActivity;
import se.zinister.chronos.Match.MatchActivity;
import se.zinister.chronos.Net.Request;
import se.zinister.chronos.Question.Question;
import se.zinister.chronos.Question.QuestionCard;
import se.zinister.chronos.R;


/**
 * Created by josef on 2015-04-21.
 */
public class MatchFragment extends BaseFragment {

    public MatchAdapter mAdapter;
    public DynamicListView mListView;
    private MatchActivity matchActivity;
    public static int gameID;
    private ArrayList<Question> list;
    private QuestionCard card;
    private FloatingActionButton fab;
    final Handler handler_interact=new Handler();//not defined as final variable. may cause        problem
    private View layout_interact;
    private boolean showInfoPressed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.match_fragment, container, false);
        matchActivity = (MatchActivity)getActivity();
        setHasOptionsMenu(true);
        gameID = MatchActivity.gameID;
        Request req = new Request(this,matchActivity.net);
        req.execute("GetQuestions", String.valueOf(MatchActivity.gameID));
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_action_accept);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int points = checkAnswer(fab);
                if (points > -1) {
                    MatchActivity m = (MatchActivity) getActivity();

                    Game.Round r = m.game.rounds.get(m.game.rounds.size() - 1);
                    if (r.oppRoundScore == -1) {
                        r.myRoundScore = points;
                        m.game.turn = !m.game.turn;
                    } else {
                        if (m.game.rounds.size() >= 5) {
                            r.myRoundScore = points;
                            m.game.turn = false;
                        } else {
                            Game.Round round = new Game.Round();
                            r.myRoundScore = points;
                            m.game.addRound(round);
                        }
                    }
                    //showYears();
                }
            }

        });
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_match, menu);
    }



    @Override
    public int getTitleResourceId() {
        return MatchActivity.MATCHPAGE;
    }


    public int checkAnswer(View fab) {
        String id = String.valueOf(gameID);
        switch(mAdapter.getCount()) {
            case 2:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1))){
                    addNextQuestion(2);
                } else {
                    flashColor();
                    fab.setVisibility(View.INVISIBLE);
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"1","0","0","0","0");
                    return 0;
                }
                break;
            case 3:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1), mAdapter.getItem(2))){
                    addNextQuestion(3);
                } else {
                    flashColor();
                    fab.setVisibility(View.INVISIBLE);
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"2","1","0","0","0");
                    return 1;
                }
                break;
            case 4:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1), mAdapter.getItem(2), mAdapter.getItem(3))){
                    addNextQuestion(4);
                } else {
                    fab.setVisibility(View.INVISIBLE);
                    flashColor();
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"2","2","1","0","0");
                    return 2;
                }
                break;
            case 5:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1), mAdapter.getItem(2), mAdapter.getItem(3),mAdapter.getItem(4))){
                    addNextQuestion(5);
                } else {
                    fab.setVisibility(View.INVISIBLE);

                    flashColor();
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"2","2","2","1","0");
                    return 3;
                }
                break;
            case 6:
                if( allCorrect(mAdapter.getItem(0),mAdapter.getItem(1), mAdapter.getItem(2), mAdapter.getItem(3),mAdapter.getItem(4),mAdapter.getItem(5))){
                    Request r = new Request(this,matchActivity.net);
                    fab.setVisibility(View.INVISIBLE);
                    r.execute("AnswerQuestions",id,"2","2","2","2","2");
                    showYears();
                    return 6;
                } else {
                    fab.setVisibility(View.INVISIBLE);
                    flashColor();
                    Request r = new Request(this,matchActivity.net);
                    r.execute("AnswerQuestions",id,"2","2","2","2","1");
                    return 5;
                }

        }
        return -1;
    }
    public void showInfo() {
        if (!showInfoPressed) {
            showInfoPressed = true;
            View root = getView();
            if (root != null) {

                View info = root.findViewById(R.id.infoView);
                info.setVisibility(View.VISIBLE);
                Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                info.setAnimation(fadeInAnimation);
                info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeInfo();
                    }
                });
            }
        }
    }

    public  void removeInfo() {
        View root = getView();
        if (root != null) {
            View info = root.findViewById(R.id.infoView);
            info.setVisibility(View.INVISIBLE);
        }
        showInfoPressed = false;
    }

    public void flashColor() {
        int c = mAdapter.getCount();
        if (c == 2) layout_interact = mListView.getViewForID(0);
        else layout_interact = mListView.getViewForID(c-1);
        Integer colorFrom = getResources().getColor(R.color.primary_color);
        Integer colorTo = getResources().getColor(R.color.wrong_answer_flash);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                layout_interact.setBackgroundColor((Integer) animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(300);
        colorAnimation.start();





        Timer timer_interact=new Timer();
        timer_interact.schedule(new TimerTask() {
            @Override
            public void run() {
                returnToWhite();
            }
        }, 600);
    }

    public void addNextQuestion(int pos) {
        if (MainActivity.DEBUG) Log.d(MainActivity.TAG, "Add new question: " + pos);
        if (pos > 2)
            list.get(pos - 1).locked = true;
        else if(pos == 2) {
            list.get(0).locked = true;
        }
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
        int c = mListView.getCount();
        if (c == 2) {
            list.get(0).locked = true;
        } else  {
            list.get(mListView.getCount()-1).locked = true;
        }
        mAdapter.notifyDataSetChanged();
        for (int i = 0; i < mListView.getCount(); i++){
            list.get(i).done = true;
        }
        fab.setVisibility(View.INVISIBLE);
        mAdapter.notifyDataSetChanged();
    }

    private void returnToWhite() {
        handler_interact.post(runnable_interact);
    }
    //creating runnable
    final Runnable runnable_interact = new Runnable() {
        public void run() {
            Integer colorFrom = getResources().getColor(R.color.wrong_answer_flash);
            Integer colorTo = getResources().getColor(R.color.white);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    layout_interact.setBackgroundColor((Integer) animator.getAnimatedValue());
                }

            });
            colorAnimation.setDuration(300);
            colorAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    showYears();
                }
            });
            colorAnimation.start();
        }
    };



    public void showQuestions(String json) {

        Decode d = new Decode();
        list = d.decodeQuestions(json);
        ArrayList <Question> tmp = new ArrayList<>();
        tmp.add(list.get(0));
        list.get(1).locked = true;
        tmp.add(list.get(1));


        mAdapter = new MatchAdapter(matchActivity, R.layout.list_item, tmp);

        View root = getView();
        if (root != null) {
            mListView = (DynamicListView) root.findViewById(R.id.questionList);
            if (mListView != null) {
                mListView.setAdapter(mAdapter);
                mListView.setCheeseList(tmp);
                mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            }

            card = new QuestionCard(getActivity());

            //Set the card inner text
            card.question = tmp.get(0).question;
            //Set card in the cardView
            CardView cardView = (CardView) root.findViewById(R.id.card);
            if (cardView != null) {
                cardView.setCard(card);
                cardView.setVisibility(View.VISIBLE);
            }

        }



    }
}
