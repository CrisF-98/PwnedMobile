package com.example.thepwnedgame;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.thepwnedgame.eventdispatcher.EventDispatcher;
import com.example.thepwnedgame.socketevents.ConnectErrorSocketEvent;
import com.example.thepwnedgame.socketevents.SocketEventImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;

public class Utilities {

    private Socket socket;
    private boolean loggedIn = false;

    static void insertFragment(AppCompatActivity activity, Fragment fragment, String tag, int fragmentId){
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(fragmentId, fragment, tag);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
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

     static Socket createSocket(Application application) throws URISyntaxException {
        Socket socket;
        //socket = IO.socket("https://pwnedgame.azurewebsites.net/socket/arcade");
        if(!isUserLoggedIn(application)){
            socket = IO.socket("https://pwnedgame.azurewebsites.net/socket/arcade");
        } else {
            /**TODO: impostare l'inserimento del token nelle options (SERVE?)*/
            final String jwt = application.getSharedPreferences("UserData", 0).getString("JWT", "");
            Map<String, List<String>> headers = new HashMap<>();
            String token = "Bearer " + jwt;
            String query = "token=" + jwt;
            headers.put("Authorization", Collections.singletonList(token));
            IO.Options options = IO.Options.builder()
                                    .setExtraHeaders(headers)
                                    .setQuery(query)
                                    .build();
            Log.d("Options", options.query);
            socket = IO.socket("https://pwnedgame.azurewebsites.net/socket/arcade", options);
        }
        return socket;
    }

    public static void eventHandler(Application application, GameActivity activity, String name, EventDispatcher eventDispatcher, Object... args) throws JSONException {
        if(args[0].getClass().getName().equals("io.socket.engineio.client.EngineIOException")){
            //refresh token
            //restart activity
        }
        try{
            if(name.equals(Socket.EVENT_CONNECT_ERROR)){
                eventDispatcher.getQueue().put(new ConnectErrorSocketEvent(application, name, (JSONObject) args[0]));
            } else {
                eventDispatcher.getQueue().put(new SocketEventImpl(name, (JSONObject) args[0]));
            }
        } catch (InterruptedException | ClassCastException e) {
            e.printStackTrace();
            //se è una class cast ho un jwt rotto.
            //quindi, refresh token e restart activity
        }
    }

    public static void setAppTopBar(AppCompatActivity activity, int title){
        Toolbar toolbar = activity.findViewById(R.id.topBar);
        toolbar.setTitle(title);
        if (activity.getSupportActionBar() == null){
            activity.setSupportActionBar(toolbar);
        }
    }

    public static void logIn(Application application){
        SharedPreferences preferences = application.getSharedPreferences("loggedIn", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("status", true);
        editor.apply();
    }

    public static void logOut(Application application){
        SharedPreferences preferences = application.getSharedPreferences("loggedIn", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("status", false);
        editor.apply();
        preferences = application.getSharedPreferences("UserData", 0);
        editor = preferences.edit();
        editor.clear().commit();
    }

    public static boolean isUserLoggedIn(Application application){
        boolean loggedIn = application.getSharedPreferences("loggedIn", 0).getBoolean("status", false);
        Log.d("loggedIn", String.valueOf(loggedIn));
        return loggedIn;
        //return false;
    }
}
