package com.example.thepwnedgame.socketevents;

import org.json.JSONObject;

public interface SocketEvent {
    String getName();
    String getContent();
    JSONObject getObjectAsJSON();
}
