package com.example.thepwnedgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thepwnedgame.leaderboards.LeaderboardAdapter;
import com.example.thepwnedgame.leaderboards.LeaderboardManager;
import com.example.thepwnedgame.leaderboards.ScoreItem;
import com.example.thepwnedgame.viewmodel.LeaderboardViewModel;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LeaderBoardsFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;
    private boolean isNetworkConnected = false;
    private static final String LEADERBOARD_TAG = "LEADERBOARD";
    private ConnectivityManager.NetworkCallback networkCallback;
    private RequestQueue requestQueue;
    private ActivityResultLauncher requestPermissionLauncher;
    private int page;
    private Snackbar snackbar;
    private LeaderboardViewModel leaderboardViewModel;
    private static final int PAGE_SIZE = 15;
    private List<ScoreItem> fullList = new ArrayList<>();
    private static final int MAX_SIZE = 1000;
    private String period = "forever";

    private final String[] engPeriods = {"forever", "day", "week", "month", "year"};
    private final String[] itaPeriods = {"sempre", "oggi", "settimana", "mese", "anno"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.leaderboards_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.page = 0;
        final Activity activity = getActivity();
        if (activity != null){
            setRecyclerView(activity);
            leaderboardViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(LeaderboardViewModel.class);
            leaderboardViewModel.getScoreItems().observe((LifecycleOwner) activity, new Observer<List<ScoreItem>>() {
                @Override
                public void onChanged(List<ScoreItem> scoreItems) {
                    adapter.setScoreItemList(scoreItems);
                }
            });
            snackbar = Snackbar.make(activity, activity.findViewById(R.id.home_frag_content), "No internet available", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if(intent.resolveActivity(activity.getPackageManager()) != null){
                                activity.startActivity(intent);
                            }
                        }
                    });
            networkCallback = new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    isNetworkConnected = true;
                    snackbar.dismiss();
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    isNetworkConnected = false;
                    snackbar.show();
                }
            };


            this.requestQueue = Volley.newRequestQueue(activity);
            registerNetworkCallback(activity);
            loadFullLeaderboard();
            loadLeaderBoards(page);
            if (page == 0){
                view.findViewById(R.id.previousleaderboard).setEnabled(false);
            }

            view.findViewById(R.id.previousleaderboard).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (page!=0){
                        page--;
                        loadLeaderBoards(page);
                        if(page==0){
                            view.findViewById(R.id.previousleaderboard).setEnabled(false);
                        }
                        view.findViewById(R.id.nextleaderboard).setEnabled(true);
                    }
                }
            });

            view.findViewById(R.id.nextleaderboard).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    page++;
                    loadLeaderBoards(page);
                    view.findViewById(R.id.previousleaderboard).setEnabled(true);
                    if(((page+1)*PAGE_SIZE) >= fullList.size()){
                        view.findViewById(R.id.nextleaderboard).setEnabled(false);
                    }
                }
            });

            Spinner periodSpinner = (Spinner) view.findViewById(R.id.period_spinner);
            periodSpinner.setOnItemSelectedListener(this);
            ArrayAdapter arrayAdapter;
            if(Locale.getDefault().getDisplayLanguage().contains("it")) {
                arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, itaPeriods);

            } else {
                arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, engPeriods);
            }
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            periodSpinner.setAdapter(arrayAdapter);
        }
    }

    private void loadFullLeaderboard() {
        final String url = "https://pwnedgame.azurewebsites.net/api/leaderboards/arcade?period="+period+"&limit="+MAX_SIZE;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final int responseItems;
                if(!fullList.isEmpty()){
                    fullList.clear();
                }
                try {
                    JSONArray responseArray = response.getJSONArray("data");
                    responseItems = responseArray.length();
                    int position = 1;
                    for (int i = 0; i < responseItems; i++){
                        JSONObject item = responseArray.getJSONObject(i);
                        fullList.add(new ScoreItem(position, item.getString("username"), item.getInt("score")));
                        position++;
                    }
                    unregisterNetworkCallback();
                    loadLeaderBoards(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Leaderboard Fragment", "error while loading leaderboards");
            }
        });
        request.setTag("LEADERBOARD");
        requestQueue.add(request);
    }

    private void loadLeaderBoards(int page) {
        int startPosition = (PAGE_SIZE * page);
        if(startPosition < fullList.size()){
            List<ScoreItem> scores = getXitemsFromStart(startPosition, PAGE_SIZE);
            leaderboardViewModel.setScoreItems(scores);
        }
    }

    private List<ScoreItem> getXitemsFromStart(int startPosition, int size) {
        List<ScoreItem> toReturn = new ArrayList<>();
        for (int j = 0; j<size && j+startPosition < fullList.size(); j++){
            toReturn.add(fullList.get(startPosition + j));
        }
        return toReturn;
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
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    connectivityManager.unregisterNetworkCallback(networkCallback);
                } else {
                    /*da marshmallow in poi*/
                    snackbar.dismiss();
                }
            }
        }
    }

    private void setRecyclerView(Activity activity) {
        recyclerView = getView().findViewById(R.id.leaderboardsRecyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new LeaderboardAdapter(activity);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterNetworkCallback();
        requestQueue.cancelAll(LEADERBOARD_TAG);
    }

    public RequestQueue getRequestQueue(){
        return this.requestQueue;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.period = engPeriods[position];
        loadFullLeaderboard();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
