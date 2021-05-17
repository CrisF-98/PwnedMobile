package com.example.thepwnedgame.leaderboards;

import android.app.Activity;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.thepwnedgame.LeaderBoardsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardManager {
    
    List<ScoreItem> fullList = new ArrayList<>();
    private static final int MAX_SIZE = 1000;
    private static final String PERIOD = "forever";

    public void loadFullList(LeaderBoardsFragment fragment){
        final String url = "https://pwnedgame.azurewebsites.net/api/leaderboards/arcade?period="+PERIOD+"&limit="+MAX_SIZE;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    final int responseItems;
                    JSONArray responseArray = response.getJSONArray("data");
                    responseItems = responseArray.length();
                    int position = 1;
                    for(int i = 0; i<responseItems; i++){
                        JSONObject jsonItem = responseArray.getJSONObject(i);
                        fullList.add(new ScoreItem(position, jsonItem.getString("username"), jsonItem.getInt("score")));
                        position++;
                    }
                    fragment.unregisterNetworkCallback();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("LeaderboardManager", "error while loading leaderboards");
            }
        });
        request.setTag("LEADERBOARD");
        fragment.getRequestQueue().add(request);
    }

    public List<ScoreItem> getFullList(){
        return this.fullList;
    }

    public List<ScoreItem> getXitemsFromStart(int start, int size){
        List<ScoreItem> returnList = new ArrayList<>();
        for(int i=start; i<=size && i<=fullList.size(); i++){
            returnList.add(this.fullList.get(i));
        }
        return returnList;
    }
}
