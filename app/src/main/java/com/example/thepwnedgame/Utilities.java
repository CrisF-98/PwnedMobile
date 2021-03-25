package com.example.thepwnedgame;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;

import io.socket.client.IO;
import io.socket.client.Socket;

public class Utilities {

    static void insertFragment(AppCompatActivity activity, Fragment fragment, String tag, int fragmentId){
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(fragmentId, fragment, tag);
        transaction.commit();
    }

    static void startMarquee(Context context, TextView textView, int marqueeAnimation){
        Animation marquee = AnimationUtils.loadAnimation(context, marqueeAnimation);
        textView.startAnimation(marquee);
    }

    static Socket createSocket() throws URISyntaxException {
        final Socket socket;
        socket = IO.socket("https://pwnedgame.azurewebsites.net/socket/arcade");
        return socket;
    }

    public static void eventHandler(String name, BlockingQueue<SocketEvent> queue, Object... args){
        try{
            queue.put(new SocketEventImpl(name, (JSONObject) args[0]));
        } catch (InterruptedException | ClassCastException e){
            e.printStackTrace();
        }
    }
}
