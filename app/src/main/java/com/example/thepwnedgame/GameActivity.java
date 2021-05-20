package com.example.thepwnedgame;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.thepwnedgame.singletons.PUTSingleton;
import com.example.thepwnedgame.socketevents.SocketEvent;
import com.example.thepwnedgame.viewmodel.PasswordViewModel;
import com.example.thepwnedgame.viewmodel.ScoreViewModel;

import org.json.JSONException;

import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.socket.client.Manager;
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
        final SharedPreferences sharedPreferences = getSharedPreferences("UserData", 0);
        /*Log.d("preferences", sharedPreferences.getString("username", ""));
        Log.d("preferences", sharedPreferences.getString("id", ""));*/
        Log.d("preferences", sharedPreferences.getString("JWT", ""));
        /*Log.d("preferences", sharedPreferences.getString("refresh", ""));*/
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
        TextView put = findViewById(R.id.game_PUT);
        put.setText(PUTSingleton.getInstance().getAPUT());

        this.eventQueue = new LinkedBlockingQueue<>();
        //connessione alla socket
        try {
            this.socket = Utilities.createSocket(getApplication());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        //creazione degli handler
        socket.on("guess", sArgs -> {
            try {
                Utilities.eventHandler(getApplication(), this,"guess", this.eventDispatcher,  sArgs);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        socket.on("on-error", sArgs -> {
            try {
                Utilities.eventHandler(getApplication(), this,  "on-error", this.eventDispatcher, sArgs);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*finish();
            startActivity(getIntent());*/
        });
        socket.on("game-end", sArgs -> {
            try {
                Utilities.eventHandler(getApplication(), this, "game-end", this.eventDispatcher, sArgs);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        socket.on(Socket.EVENT_CONNECT_ERROR, sArgs-> {
            try {
                runOnUiThread(() -> Toast.makeText(getApplication(), "FATAL ERROR - CRASHING", Toast.LENGTH_SHORT));
                Thread.sleep(100);
                Utilities.eventHandler(getApplication(), this,  Socket.EVENT_CONNECT_ERROR, this.eventDispatcher, sArgs);
            } catch (ClassCastException | JSONException | InterruptedException e) {
                e.printStackTrace();
                /*Intent gameOverIntent = new Intent(getApplicationContext(), GameOverActivity.class);
                startActivity(gameOverIntent);
                Log.d("game activity", "class cast detected in game activity, bye.");
                socket.disconnect();
                finish();*/
            }
        });
        socket.on(Manager.EVENT_ERROR, sArgs -> {
            try {
                Utilities.eventHandler(getApplication(), this,  Manager.EVENT_ERROR, this.eventDispatcher, sArgs);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("GameActivity", "ERROR DETECTED");
        });
        socket.on("error", sArgs-> {
            try {
                Utilities.eventHandler(getApplication(), this,  Socket.EVENT_CONNECT_ERROR, this.eventDispatcher, sArgs);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        //creazione observer sullo score
        TextView scoreTextView = findViewById(R.id.scoreNumber);
        this.scoreViewModel.getScore().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                //Integer score = Integer.parseInt(scoreTextView.getText().toString());
                scoreTextView.setText(Integer.toString(integer));
            }
        });
        socket.connect();
        //progress bar
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(100);
        countdown = new BarCountdown(10000, 100, this) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress((int) (millisUntilFinished/100));

            }

            @Override
            public void onFinish() {
                Intent gameOverIntent = new Intent(this.getActivity(), GameOverActivity.class);
                startActivity(gameOverIntent);
            }
        };
        //start game
        //logged version - check if there's an on-error
        if(this.eventDispatcher.getQueue().size()!=0){
            try {
                this.eventDispatcher.nextEvent(passOneViewModel, scoreViewModel, this);
            } catch (InterruptedException | JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("userdata", getSharedPreferences("UserData", 0).getString("username", ""));

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

    /*public void logout() {
        //pulisco gli eventi
        this.eventQueue.clear();
        //this.recreate();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recreate();
            }
        });
    }*/
}
