package com.example.thepwnedgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.jwt.JWT;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.internal.Util;

//import com.auth0.android.Auth0;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private final String TAG = "LOGIN";
    private RequestQueue requestQueue;
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean isNetworkConnected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        Utilities.setAppTopBar(this, R.string.login);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        networkCallback = new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                isNetworkConnected = true;
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                isNetworkConnected = false;
                Toast.makeText(getApplicationContext(), "internet lost", Toast.LENGTH_SHORT).show();
            }
        };

        this.requestQueue = Volley.newRequestQueue(this);
        registerNetworkCallback(this);
        this.usernameEditText = findViewById(R.id.usernameLoginEditText);
        this.passwordEditText = findViewById(R.id.passwordLoginEditText);
        this.loginButton = findViewById(R.id.loginActivityButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                if(!username.equals("") && !password.equals("")){
                    try {
                        login();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void login() throws JSONException {
        final String url = "https://pwnedgame.azurewebsites.net/api/users/login/";
        JSONObject body = new JSONObject();
        body.put("username", this.usernameEditText.getText());
        body.put("password", this.passwordEditText.getText());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                try {
                    JSONObject data = response.getJSONObject("data");
                    String id = data.getString("id");
                    String username = data.getString("username");
                    String JWTToken = data.getString("token");
                    Log.d("login jwt", JWTToken);
                    String refreshToken;
                    refreshToken = data.getString("refresh");
                    Log.d("login refresh", refreshToken);
                    updatePreferences(id, username, JWTToken, refreshToken);
                    Utilities.logIn(getApplication());
                    /*System.out.println(Utilities.isUserLoggedIn(getApplication()));*/
                    Toast.makeText(getApplicationContext(), "Login - Success", Toast.LENGTH_SHORT).show();
                    Intent goToHomeIntent = new Intent(getApplicationContext(), MainActivity.class);
                    /*goToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivityIfNeeded(goToHomeIntent, 0);*/
                    startActivity(goToHomeIntent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("login error", String.valueOf(error.networkResponse.statusCode));
                Toast.makeText(getApplicationContext(), "Login failure, retry", Toast.LENGTH_SHORT).show();
            }
        });
        request.setTag(TAG);
        requestQueue.add(request);
    }

    private void updatePreferences(String id, String username, String jwtToken, String refreshToken) {
        SharedPreferences preferences = this.getSharedPreferences("UserData", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", id);
        editor.putString("username", username);
        editor.putString("JWT", jwtToken);
        editor.putString("refresh", refreshToken);
        editor.apply();
    }

    private void registerNetworkCallback(Activity activity){
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
            } else {
                /*da marshmallow in poi*/
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                isNetworkConnected = networkInfo!=null && networkInfo.isConnected();
            }
        } else {
            isNetworkConnected = false;
        }
    }

    public void unregisterNetworkCallback(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } else {
                /*da marshmallow in poi*/
                Toast.makeText(this, "internet unavailable.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterNetworkCallback();
        requestQueue.cancelAll(TAG);
    }
}
