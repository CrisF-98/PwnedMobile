package com.example.thepwnedgame.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ScoreViewModel extends AndroidViewModel {

    private final Application application;
    private MutableLiveData<Integer> score = new MutableLiveData<>();

    public void setScore(Integer score, Activity activity){
        this.score.postValue(score);
        SharedPreferences sharedPreferences = activity.getSharedPreferences("Points", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("pointsNumber", score);
        editor.apply();
    }

    public LiveData<Integer> getScore(){
        return this.score;
    }

    public ScoreViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }
}
