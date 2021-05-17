package com.example.thepwnedgame.socketevents;

import android.app.Application;

import org.json.JSONObject;

public class ConnectErrorSocketEvent implements SocketEvent{

    private Application application;
    private final String name;
    private final JSONObject data;
    //private TokenHandlerSingleton tokenHandlerSingleton;

    public ConnectErrorSocketEvent(Application application, String name, JSONObject data) {
        this.application = application;
        this.name = name;
        this.data = data;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getContent() {
        return data.toString();
    }

    @Override
    public JSONObject getObjectAsJSON() {
        return data;
    }

    public void refresh(){
        //TODO:evento per fare refresh del token
    }
}
