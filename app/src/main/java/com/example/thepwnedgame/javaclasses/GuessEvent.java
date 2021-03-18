package com.example.thepwnedgame.javaclasses;

public interface GuessEvent {
    String getFirstPassword();
    int getFirstValue();
    String getSecondPassword();
    int getTimeoutMillis();
    int getScore();
    int getGuesses();
}
