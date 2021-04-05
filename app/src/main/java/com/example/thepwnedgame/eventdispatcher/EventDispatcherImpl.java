package com.example.thepwnedgame.eventdispatcher;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thepwnedgame.GameActivity;
import com.example.thepwnedgame.socketevents.GuessEvent;
import com.example.thepwnedgame.socketevents.SocketEvent;
import com.example.thepwnedgame.socketevents.SocketGuessEvent;
import com.example.thepwnedgame.viewmodel.PasswordViewModel;
import com.example.thepwnedgame.viewmodel.ScoreViewModel;

import org.json.JSONException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EventDispatcherImpl implements EventDispatcher{

    private BlockingQueue<SocketEvent> queue;

    public EventDispatcherImpl() {
        this.queue = new LinkedBlockingQueue<>();
    }

    @Override
    public void nextEvent(PasswordViewModel passwordViewModel, ScoreViewModel scoreViewModel, AppCompatActivity activity) throws InterruptedException, JSONException {
        final SocketEvent event = this.queue.take();
        final String eventName = event.getName();
        GameActivity gameActivity = (GameActivity) activity;
        if (eventName.equals("on-error")){
            //TODO: on-error
        }
        if (eventName.equals("game-end")){
            //TODO: game-end
        }
        if (eventName.equals("guess")){
            GuessEvent guessEvent = new SocketGuessEvent(event);
            passwordViewModel.setFirstPassword(guessEvent.getFirstPassword());
            passwordViewModel.setSecondPassword(guessEvent.getSecondPassword());
            passwordViewModel.setValue(guessEvent.getFirstValue());
            scoreViewModel.setScore(guessEvent.getScore());
            gameActivity.getProgressBar().setProgress(100);
            gameActivity.getCountdown().cancel();
            gameActivity.getCountdown().start();
        }
    }

    public BlockingQueue<SocketEvent> getQueue(){
        return this.queue;
    }
}
