package se.zinister.timeline.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import se.zinister.timeline.Adapters.FriendAdapter;
import se.zinister.timeline.Items.Friend;
import se.zinister.timeline.Json.Decode;
import se.zinister.timeline.MainActivity;
import se.zinister.timeline.Net.Request;
import se.zinister.timeline.R;


/**
 * Created by josef on 2015-04-21.
 */
public class FindUsersFragment extends BaseFragment {
    EditText e;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.find_users_fragment, container, false);

        setTargetFragment(this, 0);
        e = (EditText)rootView.findViewById(R.id.searchFriends);
        e.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                MainActivity m = (MainActivity) getActivity();
                Request r = new Request(getTargetFragment(),m.net);
                r.execute("Search", "Name", v.getText().toString());
                return true;
            }
        });
        return rootView;
    }

    @Override
    public int getTitleResourceId() {
        return MainActivity.FIND_FRIEND;
    }


    public void showResult(String json) {
        Decode d = new Decode();
        ArrayList<Friend> friends = d.decodeSearchList(json);
        GridView gridView = (GridView) getView().findViewById(R.id.gridViewFindFriend);
        FriendAdapter f = new FriendAdapter(getActivity(),R.layout.challenge_friend_griditem);
        f.addAll(friends);
        gridView.setAdapter(f);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendAdapter f = (FriendAdapter) parent.getAdapter();

                ImageView imageView = (ImageView) view.findViewById(R.id.profilePicture);
                TextView textView = (TextView) view.findViewById(R.id.selectionText);
                TransitionSet transitionSet = new TransitionSet();
                transitionSet.addTransition(new ChangeImageTransform());
                transitionSet.addTransition(new ChangeBounds());
                transitionSet.setDuration(300);

                Fragment fragment2 = new FriendFragment();
                fragment2.setSharedElementEnterTransition(transitionSet);
                fragment2.setSharedElementReturnTransition(transitionSet);
                Fade fade = new Fade();
                fade.setStartDelay(300);
                fragment2.setEnterTransition(fade);

                Friend friend = f.getItem(position);

                Intent i = getActivity().getIntent();
                i.putExtra("FriendName", friend.Name);

                Bitmap bitmap = drawableToBitmap(imageView.getDrawable());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                i.putExtra("FriendPicture", b);
                i.putExtra("FriendID", friend.Id);
                i.putExtra("won", friend.won);
                i.putExtra("draw", friend.draw);
                i.putExtra("lost", friend.lost);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(e.getWindowToken(), 0);


                getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment2)
                        .addToBackStack("Stack")
                        .addSharedElement(imageView, "profilePicture2")
                        .addSharedElement(textView, "friendName")
                        .commit();
            }
        });
    }


    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


}
