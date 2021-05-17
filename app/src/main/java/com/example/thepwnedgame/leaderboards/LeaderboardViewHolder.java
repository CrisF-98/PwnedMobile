package com.example.thepwnedgame.leaderboards;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thepwnedgame.R;

public class LeaderboardViewHolder extends RecyclerView.ViewHolder {

    TextView positionTextView;
    TextView usernameTextView;
    TextView scoreTextView;

    public LeaderboardViewHolder(@NonNull View itemView) {
        super(itemView);
        positionTextView = itemView.findViewById(R.id.positionTextView);
        usernameTextView = itemView.findViewById(R.id.nameTextView);
        scoreTextView = itemView.findViewById(R.id.scoreLeaderboardsTextView);
    }
}
