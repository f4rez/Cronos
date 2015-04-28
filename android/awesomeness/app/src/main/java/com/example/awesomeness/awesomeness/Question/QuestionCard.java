package com.example.awesomeness.awesomeness.Question;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.awesomeness.awesomeness.R;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Josef on 2015-04-28.
 */
public class QuestionCard  extends Card {
    public String question;

    public QuestionCard(Context context) {
        this(context, R.layout.list_item);
    }

    public QuestionCard(Context context, int innerLayout) {
        super(context, innerLayout);
        init();
    }

    private void init(){

        //No Header

        //Set a OnClickListener listener
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Toast.makeText(getContext(), "Click Listener card=", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        //Retrieve elements
        TextView mTitle = (TextView) parent.findViewById(R.id.question);




        if (mTitle!=null)
            mTitle.setText(question);


    }
}
