package com.example.thepwnedgame.eventdispatcher;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thepwnedgame.GameActivity;
import com.example.thepwnedgame.GameOverActivity;
import com.example.thepwnedgame.R;
import com.example.thepwnedgame.socketevents.GuessEvent;
import com.example.thepwnedgame.socketevents.OnErrorEvent;
import com.example.thepwnedgame.socketevents.OnErrorEventImpl;
import com.example.thepwnedgame.socketevents.SocketEvent;
import com.example.thepwnedgame.socketevents.SocketGuessEvent;
import com.example.thepwnedgame.viewmodel.PasswordViewModel;
import com.example.thepwnedgame.viewmodel.ScoreViewModel;

import org.json.JSONException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.socket.client.Manager;
import io.socket.client.Socket;

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
        if (eventName.equals("game-end")){
            //TODO: l'on-error scatta con un jwt scaduto. Lanciare il refresh del token JWT
            final Vibrator vibe = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            Intent gameOverIntent = new Intent(gameActivity, GameOverActivity.class);
            gameActivity.getCountdown().cancel();
            gameActivity.getSocket().disconnect();
            vibe.vibrate(50);
            gameActivity.startActivity(gameOverIntent);

        }
        if (eventName.equals("on-error")){
            OnErrorEvent onErrorEvent = new OnErrorEventImpl(event);
            Log.d("ERROR-CODE", String.valueOf(onErrorEvent.getErrorCode()));
            Log.d("ERROR-DESCRIPTION", onErrorEvent.getErrorDescription());
            final Vibrator vibe = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            Intent gameOverIntent = new Intent(gameActivity, GameOverActivity.class);
            gameActivity.getCountdown().cancel();
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
        if (eventName.equals(Socket.EVENT_CONNECT_ERROR) || eventName.equals(Manager.EVENT_ERROR)){
            Log.d("Event dispatcher", "ERROR DETECTED");
            //pulisco la coda di eventi
            this.queue.clear();
            /*TokenHandlerSingleton.getInstance(activity.getApplication()).refreshToken();
            ((GameActivity) activity).logout();*/
        }
    }

    public BlockingQueue<SocketEvent> getQueue(){
        return this.queue;
    }
}
