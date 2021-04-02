package com.example.thepwnedgame.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ScoreViewModel extends AndroidViewModel {

    private final Application application;
    private MutableLiveData<Integer> score = new MutableLiveData<>();

    public void setScore(Integer score){
        this.score.postValue(score);
    }

    public LiveData<Integer> getScore(){
        return this.score;
    }

    public ScoreViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }
}
