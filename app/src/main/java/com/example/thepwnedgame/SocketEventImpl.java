package com.example.thepwnedgame;

import com.example.thepwnedgame.SocketEvent;

import org.json.JSONObject;

public class SocketEventImpl implements SocketEvent {

    private final String name;
    private final JSONObject data;

    public SocketEventImpl(String name, JSONObject data) {
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
        return this.data;
    }
}
