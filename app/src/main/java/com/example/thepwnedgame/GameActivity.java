package com.example.thepwnedgame;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.thepwnedgame.eventdispatcher.EventDispatcher;
import com.example.thepwnedgame.eventdispatcher.EventDispatcherImpl;
import com.example.thepwnedgame.socketevents.GuessEvent;
import com.example.thepwnedgame.socketevents.SocketEvent;
import com.example.thepwnedgame.socketevents.SocketGuessEvent;
import com.example.thepwnedgame.viewmodel.PasswordViewModel;
import com.example.thepwnedgame.viewmodel.ScoreViewModel;

import org.json.JSONException;

import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.socket.client.Socket;


public class GameActivity extends AppCompatActivity {

    private Socket socket;
    private BlockingQueue<SocketEvent> eventQueue;
    private PasswordViewModel passOneViewModel;
    private ScoreViewModel scoreViewModel;
    private ProgressBar progressBar;
    private CountDownTimer countdown;
    private EventDispatcher eventDispatcher = new EventDispatcherImpl();


    @Override
    protected void onStart(){
        super.onStart();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);
        if (savedInstanceState == null){
            Utilities.insertFragment(this, new PasswordGameFragment(), "First Password Fragment", R.id.firstPasswordFragment);
            Utilities.insertFragment(this, new PasswordGameFragment(), "Second Password Fragment", R.id.secondPasswordFragment);
            TextView put = findViewById(R.id.game_PUT);
            Utilities.startMarquee(this, put, R.anim.marquee);
        }
        //creazione dei viewmodel
        this.passOneViewModel = new ViewModelProvider(this).get(PasswordViewModel.class);
        this.scoreViewModel = new ViewModelProvider(this).get(ScoreViewModel.class);


        this.eventQueue = new LinkedBlockingQueue<>();
        //connessione alla socket
        try {
            this.socket = Utilities.createSocket();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        //creazione degli handler
        socket.on("guess", sArgs -> Utilities.eventHandler("guess", this.eventDispatcher,  sArgs));
        socket.on("on-error", sArgs -> {
            Utilities.eventHandler("on-error", this.eventDispatcher, sArgs);
            finish();
            startActivity(getIntent());
        });
        socket.on("game-end", sArgs -> Utilities.eventHandler("game-end", this.eventDispatcher, sArgs));

        //creazione observer sullo score
        TextView scoreTextView = findViewById(R.id.scoreNumber);
        this.scoreViewModel.getScore().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Integer score = Integer.parseInt(scoreTextView.getText().toString());
                Integer newScore = score + integer;
                scoreTextView.setText(Integer.toString(newScore));
            }
        });
        //connessione della socket
        socket.connect();
        //progress bar
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(100);
        countdown = new CountDownTimer(10000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress((int) (millisUntilFinished/100));
            }

            @Override
            public void onFinish() {
                Log.d("countdown", "countdown over");
            }
        };

        //start game
        socket.emit("start");
        countdown.start();
        try {
            this.eventDispatcher.nextEvent(passOneViewModel, scoreViewModel, this);
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket(){
        return this.socket;
    }

    public EventDispatcher getEventDispatcher(){
        return this.eventDispatcher;
    }

    public PasswordViewModel getPassOneViewModel() {
        return passOneViewModel;
    }

    public ScoreViewModel getScoreViewModel() {
        return scoreViewModel;
    }

    public ProgressBar getProgressBar(){
        return this.progressBar;
    }

    public CountDownTimer getCountdown() {
        return countdown;
    }

    /*public void nextEvent() throws InterruptedException, JSONException {
        final SocketEvent event = this.eventQueue.take();
        final String eventName = event.getName();
        if (eventName.equals("on-error")){
            //TODO: on-error
        }
        if (eventName.equals("game-end")){
            //TODO: game-end
        }
        if (eventName.equals("guess")){
            GuessEvent guessEvent = new SocketGuessEvent(event);
            passOneViewModel.setFirstPassword(guessEvent.getFirstPassword());
            passOneViewModel.setSecondPassword(guessEvent.getSecondPassword());
            passOneViewModel.setValue(guessEvent.getFirstValue());
            scoreViewModel.setScore(guessEvent.getScore());
            //TODO: to be tested, should work
        }
    }*/
}
