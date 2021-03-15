package prova;


import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class Prova {


    private Socket socket;
    private boolean continua;
    public static void main(String[] args){
        try{
            new Prova();
        } catch (Exception e){
            e.printStackTrace();
        }
    }



    public Prova() throws Exception{

        continua = true;
        try{
            socket = IO.socket("https://pwnedgame.azurewebsites.net/socket/arcade");
        } catch (URISyntaxException e){
            System.out.println("url sbagliato");
        }
        socket.on("guess", onGuess);
        socket.connect();   //per connettere la socket
        socket.emit("start");
        while(continua){

        }
    }

    private Emitter.Listener onGuess = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String password1;
            String value1;
            String password2;
            String value2;
            int timeout;
            int score;
            int guesses;
            final JSONObject data = (JSONObject) args[0];
            try {
                password1 = data.getString("password1");
                value1 = data.getString("value1");
                password2 = data.getString("password2");
                value2= data.getString("value2");
                timeout = data.getInt("timeout");
                score = data.getInt("score");
                guesses = data.getInt("guesses");
            } catch (JSONException e){
                return;
            }
            System.out.println(password1 + " " + password2);
        }
    };
}
