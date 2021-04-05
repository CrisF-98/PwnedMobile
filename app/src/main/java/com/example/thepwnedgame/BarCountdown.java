package com.example.thepwnedgame;

import android.app.Activity;
import android.app.Application;
import android.os.CountDownTimer;

public abstract class BarCountdown extends CountDownTimer {

    private Activity activity;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public BarCountdown(long millisInFuture, long countDownInterval, Activity activity) {
        super(millisInFuture, countDownInterval);
        this.activity = activity;
    }

    public Activity getActivity(){
        return this.activity;
    }
}
