package com.example.thepwnedgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        Utilities.insertFragment(this, new HomeFragment(), "Main Fragment", R.id.mainActivityFragment);
        /**TextView home_put = findViewById(R.id.title_PUT);
        home_put.setSelected(true);
        TextView home_put = findViewById(R.id.title_PUT);
        Utilities.startMarquee(this, home_put, R.anim.marquee);*/
    }
}