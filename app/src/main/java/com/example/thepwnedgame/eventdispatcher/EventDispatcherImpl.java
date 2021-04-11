package com.example.thepwnedgame.eventdispatcher;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thepwnedgame.GameActivity;
import com.example.thepwnedgame.GameOverActivity;
import com.example.thepwnedgame.R;
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

    private static final String correctAnswer = "raw/correct_answer.wav";

    public EventDispatcherImpl() {
        this.queue = new LinkedBlockingQueue<>();
    }

    @Override
    public void nextEvent(PasswordViewModel passwordViewModel, ScoreViewModel scoreViewModel, AppCompatActivity activity) throws InterruptedException, JSONException {
        final SocketEvent event = this.queue.take();
        final String eventName = event.getName();
        GameActivity gameActivity = (GameActivity) activity;
        if (eventName.equals("on-error") || eventName.equals("game-end")){
            final Vibrator vibe = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            Intent gameOverIntent = new Intent(gameActivity, GameOverActivity.class);
            gameActivity.getSocket().disconnect();
            vibe.vibrate(50);
            gameActivity.startActivity(gameOverIntent);

        }
        if (eventName.equals("guess")){
            final MediaPlayer correctAnswer = MediaPlayer.create(activity, R.raw.correct_answer);
            GuessEvent guessEvent = new SocketGuessEvent(event);
            correctAnswer.start();
            passwordViewModel.setFirstPassword(guessEvent.getFirstPassword());
            passwordViewModel.setSecondPassword(guessEvent.getSecondPassword());
            passwordViewModel.setValue(guessEvent.getFirstValue());
            scoreViewModel.setScore(guessEvent.getScore(), gameActivity);
            gameActivity.getProgressBar().setProgress(100);
            gameActivity.getCountdown().cancel();
            gameActivity.getCountdown().start();
        }
    }

    public BlockingQueue<SocketEvent> getQueue(){
        return this.queue;
    }
}
