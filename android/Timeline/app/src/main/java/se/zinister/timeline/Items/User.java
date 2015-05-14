package se.zinister.timeline.Items;

/**
 * Created by Josef on 2015-05-14.
 */
public class User {
    public String id;
    public String name;
    public String picture;
    private String token;
    public int won;
    public int draw;
    public int lost;
    public int level;

    public User(String id, String name, String pic, String token, int won, int draw, int lost, int level){
        this.id = id;
        this.name = name;
        this.picture = pic;
        this.token = token;
        this.won = won;
        this.draw = draw;
        this.lost = lost;
        this.level = level;
    }
}
