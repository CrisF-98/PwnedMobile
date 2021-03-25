package com.example.thepwnedgame;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.transform.URIResolver;

import io.socket.client.Socket;


public class GameActivity extends AppCompatActivity {

    private Socket socket;
    private BlockingQueue<SocketEvent> eventQueue;

    @Override
    protected void onStart(){
        super.onStart();
        this.eventQueue = new LinkedBlockingQueue<>();
        //connessione alla socket
        try {
            this.socket = Utilities.createSocket();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        //creazione degli handler
        socket.on("guess", sArgs -> Utilities.eventHandler("guess", eventQueue, sArgs));
        socket.on("on-error", sArgs -> Utilities.eventHandler("on-error", eventQueue, sArgs));
        socket.on("game-end", sArgs -> Utilities.eventHandler("game-end", eventQueue, sArgs));
        //connessione della socket
        socket.connect();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);
        if (savedInstanceState == null){
            Utilities.insertFragment(this, new PasswordGameFragment(), "First Password Fragment", R.id.firstPasswordFragment);
            Utilities.insertFragment(this, new PasswordGameFragment(), "Second Password Fragment", R.id.secondPasswordFragment);
        }
        /**
         * TODO: Codice per scorrere la barra del tempo.
         */
        //start game
    }

}
