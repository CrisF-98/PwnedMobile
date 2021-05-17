package com.example.thepwnedgame.leaderboards;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thepwnedgame.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardViewHolder> {

    private List<ScoreItem> scoreItemList = new ArrayList<>();

    private Activity activity;

    public LeaderboardAdapter(Activity activity){
        this.activity = activity;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboards_singlelayout, parent, false);
        return new LeaderboardViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        ScoreItem item = scoreItemList.get(position);
        String itemPosition = item.getPositionAsString();
        String itemUsername = item.getUsername();
        String itemScore = item.getScoreAsString();
        holder.scoreTextView.setText(itemScore);
        holder.usernameTextView.setText(itemUsername);
        holder.positionTextView.setText(itemPosition);
    }

    @Override
    public int getItemCount() {
        return scoreItemList.size();
    }

    public void setScoreItemList(List<ScoreItem> list) {
        this.scoreItemList = list;
        notifyDataSetChanged();
    }

    public void setScoreItemListFromJSONObjects(List<JSONObject> list) throws JSONException {
        scoreItemList.clear();
        for (JSONObject object: list) {
            ScoreItem newScore = new ScoreItem(list.indexOf(object), object.getString("username"), Integer.parseInt(object.getString("score")));
            this.scoreItemList.add(newScore);
        }
    }
}
