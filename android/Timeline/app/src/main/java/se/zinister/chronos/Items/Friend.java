package se.zinister.chronos.Items;

/**
 * Created by josef on 2015-04-21.
 */
public class Friend  {

    public String Name;
    public String Id;
    public String PictureLink;
    public int won;
    public int draw;
    public int lost;
    public boolean isFriend;

    public Friend(String n, String i, String p, int w, int draw, int lost, boolean isFriend) {
        Name = n;
        Id = i;
        p = p.substring(0, p.indexOf("sz=")) + "sz=250";
        PictureLink =p;
        won=w;
        this.draw = draw;
        this.lost = lost;
        this.isFriend = isFriend;
    }





}
