package Net;

import android.os.AsyncTask;
import android.util.Log;

import com.example.awesomeness.awesomeness.MainActivity;

import java.net.HttpURLConnection;

/**
 * Created by josef on 2015-04-13.
 */
public class Request extends AsyncTask<String, Void, String> {

    private final MainActivity caller;
    NetRequests net;
    String action;

    public Request(MainActivity caller, NetRequests net) {
        this.caller = caller;
        this.net = net;
    }


    @Override
    protected String doInBackground(String... string) {
        action = string[0];
        switch (action){
         case "GetQuestions":
             String s = string[1];
             Log.d("AsyncTask","s =" + s);
             int id = Integer.parseInt(s);
             return net.getQuestions(id);
         case "JoinGame":
             return net.joinGame();
         case "AnswerQuestions":
             return net.answerQuestions(Integer.parseInt(string[1]),string[2],string[3],string[4],string[5],string[6]);
         case "RegisterUser":
             return net.registerUSer();
         case "Login":
             return net.login();
        }
        return "error";
    }

    @Override
    protected void onPostExecute(String returned) {
        switch (action){

            case "GetQuestions":
                caller.testlayout(returned);
            case "JoinGame":
                caller.testlayout(returned);
            case "AnswerQuestions":
                caller.testlayout(returned);
            case "RegisterUser":
                caller.testlayout(returned);
            case "Login":
                caller.testlayout(returned);
                default:
                    caller.testlayout(returned);

        }

    }




}
