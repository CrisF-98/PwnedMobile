package com.example.thepwnedgame.eventdispatcher;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thepwnedgame.socketevents.SocketEvent;
import com.example.thepwnedgame.viewmodel.PasswordViewModel;
import com.example.thepwnedgame.viewmodel.ScoreViewModel;

import org.json.JSONException;

import java.util.concurrent.BlockingQueue;

public interface EventDispatcher {
    void nextEvent(PasswordViewModel passwordViewModel, ScoreViewModel scoreViewModel, AppCompatActivity activity) throws InterruptedException, JSONException;
    BlockingQueue<SocketEvent> getQueue();
}
