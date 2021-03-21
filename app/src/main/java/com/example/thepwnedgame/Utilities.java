package com.example.thepwnedgame;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class Utilities {

    static void insertFragment(AppCompatActivity activity, Fragment fragment, String tag, int fragmentId){
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(fragmentId, fragment, tag);
        transaction.commit();
    }

    static void startMarquee(Context context, TextView textView, int marqueeAnimation){
        Animation marquee = AnimationUtils.loadAnimation(context, marqueeAnimation);
        textView.startAnimation(marquee);
    }

}
