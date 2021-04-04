package com.example.thepwnedgame;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.thepwnedgame.socketevents.SocketEvent;
import com.example.thepwnedgame.socketevents.SocketEventImpl;
import com.example.thepwnedgame.socketevents.SocketGuessEvent;
import com.example.thepwnedgame.viewmodel.PasswordViewModel;
import com.example.thepwnedgame.viewmodel.ScoreViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import io.socket.client.IO;
import io.socket.client.Socket;

public class Utilities {

    private Socket socket;

    static void insertFragment(AppCompatActivity activity, Fragment fragment, String tag, int fragmentId){
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(fragmentId, fragment, tag);
        transaction.commit();
    }

    static void refreshPasswordFragment(AppCompatActivity activity){
        List<Fragment> passwordFragmentList = new ArrayList<>();
        passwordFragmentList.add(activity.getSupportFragmentManager().findFragmentById(R.id.firstPasswordFragment));
        passwordFragmentList.add(activity.getSupportFragmentManager().findFragmentById(R.id.secondPasswordFragment));
        for(Fragment singleFragment: passwordFragmentList){
            activity.getSupportFragmentManager().beginTransaction().detach(singleFragment).attach(singleFragment).commit();
        }
    }

    static void startMarquee(Context context, TextView textView, int marqueeAnimation){
        Animation marquee = AnimationUtils.loadAnimation(context, marqueeAnimation);
        textView.startAnimation(marquee);
    }

     static Socket createSocket() throws URISyntaxException {
        Socket socket = IO.socket("https://pwnedgame.azurewebsites.net/socket/arcade");
        return socket;
    }

    public static void eventHandler(String name, BlockingQueue<SocketEvent> queue, Object... args){
        try{
            queue.put(new SocketEventImpl(name, (JSONObject) args[0]));
        } catch (InterruptedException | ClassCastException e){
            e.printStackTrace();
        }
    }

    public static void eventHandlerGuess(String name, BlockingQueue<SocketEvent> queue, PasswordViewModel passwordViewModel, ScoreViewModel scoreViewModel, Object... args) throws JSONException {
        SocketGuessEvent event = new SocketGuessEvent(name, (JSONObject) args[0]);
        try{
            queue.put(event);
        } catch (InterruptedException | ClassCastException e){
            e.printStackTrace();
        }
        /*passwordViewModel.setFirstPassword(event.getFirstPassword());
        passwordViewModel.setValue(event.getFirstValue());
        passwordViewModel.setSecondPassword(event.getSecondPassword());
        scoreViewModel.setScore(event.getScore());*/
    }
}
