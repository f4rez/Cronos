package se.zinister.chronos.Items;

import android.content.ClipData;

/**
 * Created by Josef on 2015-02-04.
 */
public class DrawerItem extends ClipData.Item {

    public String name;
    public int picID;

    public DrawerItem(CharSequence text) {
        super(text);
    }

    public DrawerItem(CharSequence text, String name, int picID) {
        super(text);
        this.name = name;
        this.picID = picID;
    }
}
