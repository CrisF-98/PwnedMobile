package com.example.thepwnedgame.singletons;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class TokenHandlerSingleton {

    private static TokenHandlerSingleton instance = null;
    private RequestQueue requestQueue;
    public static final String TAG = "REFRESH";
    private ConnectivityManager.NetworkCallback networkCallback;
    private Application application;
    private boolean isNetworkConnected = false;
    private String jwt;
    private String refresh;

    private TokenHandlerSingleton(Application application){
        this.application = application;
        this.requestQueue =  Volley.newRequestQueue(application);
    }

    public static TokenHandlerSingleton getInstance(Application application){
        if(instance == null){
            instance = new TokenHandlerSingleton(application);
        }
        return instance;
    }

    public void refreshToken() throws JSONException {
        final String url = "https://pwnedgame.azurewebsites.net/api/users/refresh";
        SharedPreferences sharedPreferences = application.getSharedPreferences("UserData", 0);
        JSONObject body = new JSONObject();
        Log.d("Singleton", sharedPreferences.getString("JWT", ""));
        Log.d("Singleton", sharedPreferences.getString("refresh", ""));
        body.put("token", sharedPreferences.getString("JWT", ""));
        body.put("refresh", sharedPreferences.getString("refresh", ""));
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    JSONObject data = response.getJSONObject("data");
                    jwt = data.getString("token");
                    refresh = data.getString("refresh");
                    editor.putString("JWT", jwt);
                    editor.putString("refresh", refresh);
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Singleton", "an error occured, Code " + error.networkResponse.statusCode);
            }
        });
        request.setTag(TAG);
        requestQueue.add(request);
    }

    public RequestQueue getRequestQueue(){
        return this.requestQueue;
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

    public void unregisterNetworkCallback(Application application){
        ConnectivityManager connectivityManager = (ConnectivityManager)application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } else {
                /*da marshmallow in poi*/
                Toast.makeText(application, "internet unavailable.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
