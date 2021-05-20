package com.example.thepwnedgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameover_layout);

        Button button = findViewById(R.id.backToHomeButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToHomeIntent = new Intent(v.getContext(), MainActivity.class);
                finish();
                startActivity(backToHomeIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("Points", 0);
        Integer score = sharedPreferences.getInt("pointsNumber", 0);
        TextView pointsNumber = findViewById(R.id.pointsNumberTextView);
        pointsNumber.setText(Integer.toString(score));
    }
}
