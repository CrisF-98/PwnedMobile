package com.example.thepwnedgame.javaclasses;

import org.json.JSONException;
import org.json.JSONObject;

public class SocketGuessEvent extends SocketEventImpl implements GuessEvent{

    private final String firstPassword;
    private final String secondPassword;
    private final int firstValue;
    private final int guesses;
    private final int score;



    public SocketGuessEvent(String name, JSONObject data) throws JSONException {
        super(name, data);
        if(!name.equals("guess")) {
            throw new IllegalArgumentException("event is not guess");
        }
        JSONObject json = super.getObjectAsJSON();
        this.firstPassword = json.getString("password1");
        this.secondPassword = json.getString("password2");
        this.firstValue = json.getInt("value1");
        this.guesses = json.getInt("guesses");
        this.score = json.getInt("score");
    }

    public SocketGuessEvent(SocketEvent event) throws JSONException {
        this(event.getName(), event.getObjectAsJSON());
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
        throw new RuntimeException("not implemented");
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
