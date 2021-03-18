package com.example.thepwnedgame.javaclasses;

import org.json.JSONObject;

public interface SocketEvent {
    String getName();
    String getContent();
    JSONObject getObjectAsJSON();
}
