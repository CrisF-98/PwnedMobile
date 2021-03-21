package com.example.thepwnedgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utilities.insertFragment(this, new HomeFragment(), "Main Fragment", R.id.mainActivityFragment);
        /**TextView home_put = findViewById(R.id.title_PUT);
        home_put.setSelected(true);*/
    }
}