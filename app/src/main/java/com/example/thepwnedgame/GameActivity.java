package com.example.thepwnedgame;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.thepwnedgame.socketevents.SocketEvent;
import com.example.thepwnedgame.viewmodel.PasswordViewModel;

import org.json.JSONException;

import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.socket.client.Socket;


public class GameActivity extends AppCompatActivity {

    private Socket socket;
    private BlockingQueue<SocketEvent> eventQueue;
    private PasswordViewModel passOneViewModel;
    private PasswordViewModel passTwoViewModel;

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
        this.passTwoViewModel = new ViewModelProvider(this).get(PasswordViewModel.class);

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
                Utilities.eventHandlerGuess("guess", eventQueue, this.passOneViewModel, this.passTwoViewModel, sArgs);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        socket.on("on-error", sArgs -> Utilities.eventHandler("on-error", eventQueue, sArgs));
        socket.on("game-end", sArgs -> Utilities.eventHandler("game-end", eventQueue, sArgs));
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
