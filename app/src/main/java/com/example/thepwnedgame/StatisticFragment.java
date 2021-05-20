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
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thepwnedgame.leaderboards.ScoreItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.view.View.GONE;

public class StatisticFragment extends Fragment {

    private boolean isNetworkConnected = false;
    private static final String TAG = "STATS";
    private ConnectivityManager.NetworkCallback networkCallback;
    private RequestQueue requestQueue;
    private List<ScoreItem> fullScores = new ArrayList<>();
    private static final int MAX_SIZE = 1000;
    private static final String PERIOD = "forever";
    private String userId;
    private String jwt;
    private String usernameString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stat_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Activity activity = getActivity();
        if (activity != null){
            if(Utilities.isUserLoggedIn(activity.getApplication())) {
                jwt = activity.getSharedPreferences("UserData", 0).getString("JWT", "");
                Log.d("first jwt", jwt);
                userId = activity.getSharedPreferences("UserData", 0).getString("id", "");
                usernameString = " " + activity.getSharedPreferences("UserData", 0).getString("username", "");
                TextView username = view.findViewById(R.id.usernameStatsTextView);
                username.setText(usernameString);
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
                this.requestQueue = Volley.newRequestQueue(getContext());
                registerNetworkCallback(activity);
                loadtop1000();
                loadscores(view);
            } else {
                if(Locale.getDefault().getDisplayLanguage().contains("it")) {
                    String text = "Devi essere loggato per visualizzare le statistiche.";
                    ((TextView) view.findViewById(R.id.helloTextView)).setText(text);
                } else {
                    String text = "You need to be logged in to view stats.";
                    ((TextView) view.findViewById(R.id.helloTextView)).setText(text);
                }
                view.findViewById(R.id.usernameStatsTextView).setVisibility(GONE);
                view.findViewById(R.id.yourAverageTextView).setVisibility(GONE);
                view.findViewById(R.id.averagePoints).setVisibility(GONE);
                view.findViewById(R.id.averageGuessesTextView).setVisibility(GONE);
                view.findViewById(R.id.averageGuesses).setVisibility(GONE);
                view.findViewById(R.id.averageMaxDivider).setVisibility(GONE);
                view.findViewById(R.id.maxScoreTextView).setVisibility(GONE);
                view.findViewById(R.id.highestScore).setVisibility(GONE);
                view.findViewById(R.id.highestComment).setVisibility(GONE);
                view.findViewById(R.id.maxTop3Divider).setVisibility(GONE);
                view.findViewById(R.id.gamesPlayedtextView).setVisibility(GONE);
                view.findViewById(R.id.gamesPlayed).setVisibility(GONE);
                view.findViewById(R.id.top3TextView).setVisibility(GONE);
                view.findViewById(R.id.firstPlaceholder).setVisibility(GONE);
                view.findViewById(R.id.firstScore).setVisibility(GONE);
                view.findViewById(R.id.secondPlaceholder).setVisibility(GONE);
                view.findViewById(R.id.secondScore).setVisibility(GONE);
                view.findViewById(R.id.thirdPlaceholder).setVisibility(GONE);
                view.findViewById(R.id.thirdScore).setVisibility(GONE);
            }
        }
    }

    private void loadscores(View view) {
        final String url = "https://pwnedgame.azurewebsites.net/api/users/"+userId+"/scores/arcade?limit="+MAX_SIZE+"&sortby=score";
        final String refreshUrl = "https://pwnedgame.azurewebsites.net/api/users/refresh";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject meta = response.getJSONObject("meta");
                    JSONArray data = response.getJSONArray("data");
                    TextView gamesPlayed = view.findViewById(R.id.gamesPlayed);
                    Integer games = Integer.parseInt(meta.getString("total"));
                    gamesPlayed.setText(meta.getString("total"));
                    switch (games) {
                        case 0: {
                            view.findViewById(R.id.firstPlaceholder).setVisibility(GONE);
                            view.findViewById(R.id.firstScore).setVisibility(GONE);
                            view.findViewById(R.id.secondPlaceholder).setVisibility(GONE);
                            view.findViewById(R.id.secondScore).setVisibility(GONE);
                            view.findViewById(R.id.thirdPlaceholder).setVisibility(GONE);
                            view.findViewById(R.id.thirdScore).setVisibility(GONE);
                            break;
                        }
                        case 1:{
                            JSONObject firstScore = data.getJSONObject(0);
                            TextView first = view.findViewById(R.id.firstScore);
                            first.setText(firstScore.getString("score"));
                            view.findViewById(R.id.secondPlaceholder).setVisibility(GONE);
                            view.findViewById(R.id.secondScore).setVisibility(GONE);
                            view.findViewById(R.id.thirdPlaceholder).setVisibility(GONE);
                            view.findViewById(R.id.thirdScore).setVisibility(GONE);
                            break;
                        }
                        case 2:{
                            JSONObject firstScore = data.getJSONObject(0);
                            JSONObject secondScore = data.getJSONObject(1);
                            TextView first = view.findViewById(R.id.firstScore);
                            first.setText(firstScore.getString("score"));
                            TextView second = view.findViewById(R.id.secondScore);
                            second.setText(secondScore.getString("score"));
                            view.findViewById(R.id.thirdPlaceholder).setVisibility(GONE);
                            view.findViewById(R.id.thirdScore).setVisibility(GONE);
                            break;
                        }
                        default:{
                            JSONObject firstScore = data.getJSONObject(0);
                            JSONObject secondScore = data.getJSONObject(1);
                            JSONObject thirdScore = data.getJSONObject(2);
                            TextView first = view.findViewById(R.id.firstScore);
                            first.setText(firstScore.getString("score"));
                            TextView second = view.findViewById(R.id.secondScore);
                            second.setText(secondScore.getString("score"));
                            TextView third = view.findViewById(R.id.thirdScore);
                            third.setText(thirdScore.getString("score"));
                        }
                    }
                    //loadstats(view);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("stats", "error");
                jwt = getActivity().getSharedPreferences("UserData", 0).getString("JWT", "");
                //TODO: richiesta di refresh, in onResponse rifare la richiesta
                JSONObject body = new JSONObject();
                try {
                    body.put("token", jwt);
                    body.put("refresh", getActivity().getSharedPreferences("UserData", 0).getString("refresh", ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest refresh = new JsonObjectRequest(Request.Method.POST, refreshUrl, body, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences preferences = getActivity().getSharedPreferences("UserData", 0);
                        SharedPreferences.Editor editor = preferences.edit();
                        try {
                            editor.putString("JWT", response.getJSONObject("data").getString("token"))
                                    .putString("refresh", response.getJSONObject("data").getString("refresh"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.commit();
                        //retry request
                        loadscores(view);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "FATAL ERROR", Toast.LENGTH_SHORT).show();
                        Utilities.logOut(getActivity().getApplication());
                        Intent goToLogin = new Intent(getActivity(), LoginActivity.class);
                        startActivity(goToLogin);
                        getActivity().finish();
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
        RetryPolicy policy = new DefaultRetryPolicy(2000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }

    private void loadstats(View view) {
        final String url = "https://pwnedgame.azurewebsites.net/api/users/"+userId+"/stats?period="+PERIOD+"&limit=30";
        final String refreshUrl = "https://pwnedgame.azurewebsites.net/api/users/refresh";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    JSONArray history = data.getJSONArray("history");
                    JSONObject firstHistory = history.getJSONObject(0);
                    TextView pointsAverage = view.findViewById(R.id.averagePoints);
                    DecimalFormat format = new DecimalFormat("#.###");
                    Double avgPoints = Double.parseDouble(firstHistory.getString("avgScore"));
                    pointsAverage.setText(format.format(avgPoints));
                    TextView averageGuesses = view.findViewById(R.id.averageGuesses);
                    Double avgGuesses = Double.parseDouble(firstHistory.getString("avgGuesses"));
                    averageGuesses.setText(String.valueOf(format.format(avgGuesses)));
                    TextView maxScore = view.findViewById(R.id.highestScore);
                    String max = firstHistory.getString("maxScore");
                    maxScore.setText(max);
                    //cerca della combinazione username-punteggio nella classifica
                    boolean found = false;
                    String user = getActivity().getSharedPreferences("UserData", 0).getString("username", "");
                    for (int i = 0; i < fullScores.size(); i++){
                        if(user.equals(fullScores.get(i).getUsername().toLowerCase()) && Integer.parseInt(max) == fullScores.get(i).getScore()){
                            found = true;
                            String languageCode = Locale.getDefault().getLanguage();
                            if (languageCode.contains("it")){
                                String toDisplay = "Posizione " + fullScores.get(i).getPosition() + " in top 1000! Prova a migliorarti!";
                                ((TextView)view.findViewById(R.id.highestComment)).setText(toDisplay);
                            } else {
                                String toDisplay = "Position " + fullScores.get(i).getPosition() + " in top 1000! Try to get better!";
                                ((TextView)view.findViewById(R.id.highestComment)).setText(toDisplay);
                            }
                            break;
                        }
                    }
                    if (!found){
                        if (Locale.getDefault().getDisplayLanguage().contains("it")){
                            String toDisplay = "Non sei ancora in top 1000, prova a entrarci!";
                            ((TextView)view.findViewById(R.id.highestComment)).setText(toDisplay);
                        } else {
                            String toDisplay = "You're not in top 1000 yet, try to get in there!";
                            ((TextView)view.findViewById(R.id.highestComment)).setText(toDisplay);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //try {
                    //TokenHandlerSingleton.getInstance(getActivity().getApplication()).refreshToken();
                    jwt = getActivity().getSharedPreferences("UserData", 0).getString("JWT", "");
                    Log.d("refreshing", jwt);
                    JSONObject body = new JSONObject();
                    try {
                        body.put("token", jwt);
                        body.put("refresh", getActivity().getSharedPreferences("UserData", 0).getString("refresh", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest retryRequest = new JsonObjectRequest(Request.Method.POST, refreshUrl, body, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            SharedPreferences preferences = getActivity().getSharedPreferences("UserData", 0);
                            SharedPreferences.Editor editor = preferences.edit();
                            try {
                                editor.putString("JWT", response.getJSONObject("data").getString("token"))
                                        .putString("refresh", response.getJSONObject("data").getString("refresh"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            editor.commit();
                            loadstats(view);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "FATAL ERROR", Toast.LENGTH_SHORT).show();
                            Utilities.logOut(getActivity().getApplication());
                            Intent goToLogin = new Intent(getActivity(), LoginActivity.class);
                            startActivity(goToLogin);
                            getActivity().finish();
                        }
                    });
                    retryRequest.setTag(TAG);
                    requestQueue.add(retryRequest);
                /*} catch (JSONException e) {
                    e.printStackTrace();
                }*/
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
        RetryPolicy policy = new DefaultRetryPolicy(2000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        request.setTag(TAG);
        requestQueue.add(request);
    }

    private void loadtop1000() {
        final String url = "https://pwnedgame.azurewebsites.net/api/leaderboards/arcade?period="+PERIOD+"&limit=1000";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final int responseItems;
                try {
                    JSONArray responseArray = response.getJSONArray("data");
                    responseItems = responseArray.length();
                    int position = 1;
                    for (int i = 0; i < responseItems; i++){
                        JSONObject item = responseArray.getJSONObject(i);
                        fullScores.add(new ScoreItem(position, item.getString("username"), item.getInt("score")));
                        position++;
                    }
                    loadstats(getView());
                    unregisterNetworkCallback();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Statistics", "error while loading leaderboards");
            }
        });
        request.setTag(TAG);
        requestQueue.add(request);
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

    @Override
    public void onStop() {
        super.onStop();
        unregisterNetworkCallback();
        if(Utilities.isUserLoggedIn(getActivity().getApplication())) {
            requestQueue.cancelAll(TAG);
        }
    }
}
