package se.zinister.chronos.Question;

/**
 * Created by josef on 2015-04-06.
 */
public class Question  {

    public String question;
    public Integer id;
    public Integer level;
    public Integer year;
    public boolean locked;
    public boolean done;


    public Question(String q, int l, int i, int y) {
        question = q;
        level = l;
        id = i;
        year = y;
        locked= false;
        done = false;
    }



    public boolean happendBefore(Question q) {
        return year <= q.year;
    }



}
