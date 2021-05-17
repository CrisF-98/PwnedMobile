package com.example.thepwnedgame.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.thepwnedgame.leaderboards.ScoreItem;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardViewModel extends AndroidViewModel {

    private MutableLiveData<List<ScoreItem>> scoreItems = new MutableLiveData<>();

    public LeaderboardViewModel(@NonNull Application application) {
        super(application);
        scoreItems.setValue(new ArrayList<>());
    }

    public LiveData<List<ScoreItem>> getScoreItems(){
        return scoreItems;
    }


    public void setScoreItems(List<ScoreItem> list){
        scoreItems.setValue(list);
    }

    private void addScoreItem(ScoreItem item){
        /*ArrayList<ScoreItem> list = new ArrayList<>();
        list.add(item);
        if (scoreItems.getValue() != null){
            list.addAll(scoreItems.getValue());
        }
        scoreItems.setValue(list);*/
        scoreItems.getValue().add(item);
    }
}
