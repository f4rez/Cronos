package se.zinister.chronos.Items;

import java.util.ArrayList;

/**
 * Created by josef on 2015-05-19.
 *
 */
public class Profile {
    public ArrayList<Friend> friends;
    public int won;
    public int lost;
    public int draw;

    public Profile(ArrayList<Friend> f, int w, int l, int d) {
        friends= f;
        won = w;
        draw = d;
        lost = l;
    }
}
