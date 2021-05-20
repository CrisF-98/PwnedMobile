package com.example.thepwnedgame.socketevents;

import org.json.JSONException;
import org.json.JSONObject;

public class SocketGameErrorEvent extends SocketEventImpl implements GuessEvent, GameEndEvent{

    private final String firstPassword, secondPassword;
    private final int firstValue, secondValue, guesses, score;

    public SocketGameErrorEvent(String name, JSONObject data) throws JSONException {
        super(name, data);
        if (!name.equals("game-end")){
            throw new IllegalStateException("not a game-end event...");
        }
        JSONObject json = super.getObjectAsJSON();
        this.firstPassword = json.getString("password1");
        this.secondPassword = json.getString("password2");
        this.firstValue = json.getInt("value1");
        this.secondValue = json.getInt("value2");
        this.guesses = json.getInt("guesses");
        this.score = json.getInt("score");
    }

    public SocketGameErrorEvent(SocketEvent event) throws JSONException {
        this(event.getName(), event.getObjectAsJSON());
    }

    @Override
    public int getSecondPasswordScore() {
        return this.secondValue;
    }

    @Override
    public String getFirstPassword() {
        return this.firstPassword;
    }

    @Override
    public int getFirstValue() {
        return this.firstValue;
    }

    @Override
    public String getSecondPassword() {
        return this.secondPassword;
    }

    @Override
    public int getTimeoutMillis() {
        return 0;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public int getGuesses() {
        return this.guesses;
    }
}
