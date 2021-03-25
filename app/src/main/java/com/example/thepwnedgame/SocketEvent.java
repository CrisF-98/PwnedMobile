package com.example.thepwnedgame;

import org.json.JSONObject;

public interface SocketEvent {
    String getName();
    String getContent();
    JSONObject getObjectAsJSON();
}
