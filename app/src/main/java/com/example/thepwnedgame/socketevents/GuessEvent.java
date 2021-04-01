package com.example.thepwnedgame.socketevents;

public interface GuessEvent {
    String getFirstPassword();
    int getFirstValue();
    String getSecondPassword();
    int getTimeoutMillis();
    int getScore();
    int getGuesses();
}
