package com.example.thepwnedgame;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.thepwnedgame.socketevents.SocketEvent;
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
        socket.on("guess", sArgs -> {
            try {
                Utilities.eventHandlerGuess("guess", eventQueue, this.passOneViewModel, this.scoreViewModel,  sArgs);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        socket.on("on-error", sArgs -> Utilities.eventHandler("on-error", eventQueue, sArgs));
        socket.on("game-end", sArgs -> Utilities.eventHandler("game-end", eventQueue, sArgs));

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
        /**
         * TODO: Codice per scorrere la barra del tempo.
         * Per mettere in comunicazione GameActivity e Fragment, utilizza ViewModel
         */
        //start game
        socket.emit("start");

    }

}
