package com.example.thepwnedgame.javaclasses;


import com.example.thepwnedgame.socketevents.GuessEvent;
import com.example.thepwnedgame.socketevents.SocketEvent;
import com.example.thepwnedgame.socketevents.SocketEventImpl;
import com.example.thepwnedgame.socketevents.SocketGuessEvent;

import io.socket.client.IO;
import io.socket.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Prova {
    //coda di eventi
    private static BlockingQueue<SocketEvent> eventQueue = new LinkedBlockingQueue<>();

    /**
     * handler di eventi
     * @param name il nome dell'evento
     * @param args  gli argomenti dell'evento
     * prova a inserire l'evento in coda
     */
    public static void handler(String name, Object... args){
        try{
            eventQueue.put(new SocketEventImpl(name, (JSONObject)args[0]));
        }catch (InterruptedException | ClassCastException e){
            e.printStackTrace();
        }
    }

    public static void main(String... args) throws URISyntaxException, InterruptedException, JSONException {
        final Socket socket = IO.socket("https://pwnedgame.azurewebsites.net/socket/arcade");
        //creo gli handler per gli eventi specifici
        socket.on("guess", sArgs->handler("guess", sArgs));
        socket.on("on-error", sArgs->handler("on-error", sArgs));
        socket.on("game-end", sArgs->handler("game-end", sArgs));
        //connetto la socket, definiti tutti gli handler
        socket.connect();
        //faccio partire il gioco
        socket.emit("start");
        boolean keepPlaying = true;
        Scanner scanner = new Scanner(System.in);
        while(keepPlaying){
            //prendo il prossimo evento dalla coda
            SocketEvent nextEvent = eventQueue.take();
            //se l'evento è "on-error", termino il gioco
            if(nextEvent.getName().equals("on-error")){
                System.out.println(nextEvent.getContent());
                keepPlaying = false;
            } else if (nextEvent.getName().equals("game-end")){
                //stessa cosa se è un game-end
                System.out.println("wrong answer");
                keepPlaying = false;
            } else if(nextEvent.getName().equals("guess")){
                //altrimenti, se è un guess
                GuessEvent guessEvent = new SocketGuessEvent(nextEvent);
                System.out.println("Next: 1: " + guessEvent.getFirstPassword() + " 2: " + guessEvent.getSecondPassword() + " Type 1 or 2 ");
                String answer = null;
                while (answer == null){
                    int typed = scanner.nextInt();
                    if (typed == 1 || typed == 2){
                        answer = Integer.toString(typed);
                    }
                }
                //invio la risposta.
                socket.emit("answer", new JSONObject("{ higher: " + answer + " }"));

            }
        }
        //fine gioco
        System.out.println("GAME OVER");
        //disconnetto la socket
        socket.disconnect();
    }
}
