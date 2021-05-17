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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thepwnedgame.singletons.PUTSingleton;
import com.example.thepwnedgame.singletons.TokenHandlerSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TitleFragment extends Fragment {

    private RequestQueue requestQueue;
    private final String TAG = "PLAY";
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean isNetworkConnected = false;

    TokenHandlerSingleton tokenHandlerSingleton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.title_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView home_put = getView().findViewById(R.id.title_PUT);
        home_put.setText(PUTSingleton.getInstance().getAPUT());
        Utilities.startMarquee(getView().getContext(), home_put, R.anim.marquee);
        final Activity activity = getActivity();
        tokenHandlerSingleton = TokenHandlerSingleton.getInstance(getActivity().getApplication());
        if (activity != null) {
            if (!Utilities.isUserLoggedIn(getActivity().getApplication())) {
                view.findViewById(R.id.logoutButton).setVisibility(View.GONE);
                view.findViewById(R.id.login_button).setVisibility(View.VISIBLE);
                view.findViewById(R.id.register_button).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.logoutButton).setVisibility(View.VISIBLE);
                view.findViewById(R.id.login_button).setVisibility(View.GONE);
                view.findViewById(R.id.register_button).setVisibility(View.GONE);
                ConstraintLayout layout = view.findViewById(R.id.title_layout);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(R.id.title_PUT, ConstraintSet.TOP, R.id.logoutButton, ConstraintSet.BOTTOM);
                constraintSet.applyTo(layout);
            }
            view.findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: refresh check
                /* SNIPPET: try {crist
                    tokenHandlerSingleton.refreshToken();
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                    Log.d("title", "click su play");
                    if(Utilities.isUserLoggedIn(activity.getApplication())){
                        //chiamata alle api per potenzialmente scatenare un refresh del token
                        final String userId = activity.getSharedPreferences("UserData", 0).getString("id", "");
                        final String period = "forever";
                        final String url = "https://pwnedgame.azurewebsites.net/api/users/"+userId+"/stats?period="+period+"&limit=30";
                        final String refreshUrl = "https://pwnedgame.azurewebsites.net/api/users/refresh";
                        requestQueue = Volley.newRequestQueue(getContext());
                        Toast.makeText(getContext(), "Please wait...", Toast.LENGTH_SHORT).show();
                        networkCallback = new ConnectivityManager.NetworkCallback() {
                            @Override
                            public void onAvailable(@NonNull Network network) {
                                super.onAvailable(network);
                                isNetworkConnected = true;
                            }

                            @Override
                            public void onLost(@NonNull Network network) {
                                super.onLost(network);
                                isNetworkConnected = false;
                                Toast.makeText(getContext(), "Internet lost...", Toast.LENGTH_SHORT).show();
                            }
                        };
                        registerNetworkCallback(activity);
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //successful request!
                                startGame();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //request per fare refresh: on response, update del JWT. onError, Fatal e crash
                                Log.d("PLAY-REFRESH", activity.getSharedPreferences("UserData", 0).getString("JWT", ""));
                                Log.d("PLAY-REFRESH", activity.getSharedPreferences("UserData", 0).getString("refresh", ""));
                                JSONObject body = new JSONObject();
                                try {
                                    body.put("token", activity.getSharedPreferences("UserData", 0).getString("JWT", ""));
                                    body.put("refresh", activity.getSharedPreferences("UserData", 0).getString("refresh", ""));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                JsonObjectRequest refresh = new JsonObjectRequest(Request.Method.POST, refreshUrl, body, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        SharedPreferences preferences = activity.getSharedPreferences("UserData", 0);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        try {
                                            editor.putString("JWT", response.getJSONObject("data").getString("token"))
                                                    .putString("refresh", response.getJSONObject("data").getString("refresh"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        editor.commit();
                                        Log.d("PLAY-REFRESH", activity.getSharedPreferences("UserData", 0).getString("JWT", ""));
                                        Log.d("PLAY-REFRESH", "refreshed");
                                        startGame();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getContext(), "FATAL ERROR, you've been logged out", Toast.LENGTH_SHORT).show();
                                        Utilities.logOut(activity.getApplication());
                                        Intent goToLogin = new Intent(activity, LoginActivity.class);
                                        startActivity(goToLogin);
                                        activity.finish();
                                    }
                                });
                                refresh.setTag(TAG);
                                requestQueue.add(refresh);
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Authorization", "Bearer " + getActivity().getSharedPreferences("UserData", 0).getString("JWT", ""));
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                return headers;
                            }
                        };
                        request.setTag(TAG);
                        requestQueue.add(request);
                    } else {
                        startGame();
                    }

                }
            });
            view.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(loginIntent);
                }
            });
            view.findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent registerIntent = new Intent(getActivity(), RegisterActivity.class);
                    startActivity(registerIntent);
                }
            });
            view.findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.logOut(getActivity().getApplication());
                    getActivity().recreate();
                }
            });
        }
    }

    private void startGame() {
        Intent startGameIntent = new Intent(getActivity(), GameActivity.class);
        startActivity(startGameIntent);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(Utilities.isUserLoggedIn(getActivity().getApplication())){
            if(requestQueue!=null){
                requestQueue.cancelAll(TAG);
                unregisterNetworkCallback();
            }
        }
        //tokenHandlerSingleton.getRequestQueue().cancelAll(TokenHandlerSingleton.TAG);
    }

    private void registerNetworkCallback(Activity activity) {
        Log.d("LAB","registerNetworkCallback");
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
            } else {
                //Class deprecated since API 29 (android 10) but used for android 6
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                isNetworkConnected = networkInfo != null && networkInfo.isConnected();
            }
        } else {
            isNetworkConnected = false;
        }
    }

    public void unregisterNetworkCallback(){
        if(getActivity() != null){
            if(Utilities.isUserLoggedIn(getActivity().getApplication())) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        connectivityManager.unregisterNetworkCallback(networkCallback);
                    } else {
                        /*da marshmallow in poi*/
                    }
                }
            }
        }
    }
}
