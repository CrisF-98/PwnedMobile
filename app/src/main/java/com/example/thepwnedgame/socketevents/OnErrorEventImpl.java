package com.example.thepwnedgame.socketevents;

import org.json.JSONException;
import org.json.JSONObject;

public class OnErrorEventImpl extends SocketEventImpl implements OnErrorEvent{

    private final int errorCode;
    private final String getErrorDescription;

    public OnErrorEventImpl(String name, JSONObject data) throws JSONException {
        super(name, data);
        if (!name.equals("on-error")) {
            throw new IllegalArgumentException("event is not on-error");
        }
        JSONObject json = super.getObjectAsJSON();
        this.errorCode = json.getInt("code");
        this.getErrorDescription = json.getString("description");
    }

    public OnErrorEventImpl(SocketEvent event) throws JSONException {
        this(event.getName(), event.getObjectAsJSON());
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorDescription() {
        return this.getErrorDescription;
    }
}
