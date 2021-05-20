package com.example.thepwnedgame;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.thepwnedgame.viewmodel.PasswordViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import io.socket.client.Socket;
import io.socket.engineio.client.EngineIOException;
import okhttp3.internal.Util;

public class PasswordGameFragment extends Fragment {

    private int fragmentNumber;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.password_fragment_layout, container,false);
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**Verifica: mettere i testi giusti al fragment --> ViewModel*/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Activity activity = getActivity();

        TextView passTextView = view.findViewById(R.id.passwordTextView);
        TextView passwordStrength = view.findViewById(R.id.passwordStrengthTextView);
        PasswordViewModel passwordViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(PasswordViewModel.class);

        if(this.getId()==R.id.firstPasswordFragment){
            this.fragmentNumber = 1;
            passwordViewModel.getFirstPassword().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    passTextView.setText(s);
                }
            });
            passwordViewModel.getValue().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    passwordStrength.setText(Integer.toString(integer));
                }
            });
        } else {
            this.fragmentNumber = 2;
            passwordViewModel.getSecondPassword().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    passTextView.setText(s);
                }
            });
            passwordStrength.setText("***");
            passwordViewModel.getValue2().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    passwordStrength.setText(s);
                    String message;
                    TextView remaining = getActivity().findViewById(R.id.timeTextView);
                    if (Locale.getDefault().getDisplayLanguage().contains("it")){
                        message = "Risposta sbagliata";
                    } else {
                        message = "Wrong answer";
                    }
                    remaining.setText(message);
                }
            });
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameActivity parentActivity = (GameActivity) getActivity();
                //Log.d("Password", "parent class: " + parentActivity.getClass().getName());
                //emissione evento
                try {
                    Socket socket = parentActivity.getSocket();
                    parentActivity.getSocket().emit("answer", new JSONObject("{ higher: " + fragmentNumber + " }"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Log.d("Password", "event emitted");
                try {
                    parentActivity.getEventDispatcher().nextEvent(parentActivity.getPassOneViewModel(), parentActivity.getScoreViewModel(), parentActivity);
                } catch (InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
                //Utilities.refreshPasswordFragment(parentActivity);
            }
        });

    }
}
