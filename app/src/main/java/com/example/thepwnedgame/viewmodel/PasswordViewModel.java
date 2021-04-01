package com.example.thepwnedgame.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class PasswordViewModel extends AndroidViewModel {

    private final Application application;
    private final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<Integer> value = new MutableLiveData<>();

    public void setPassword(String password){
        this.password.postValue(password);
    }

    public void setValue(Integer value){
        this.value.postValue(value);
    }

    public LiveData<String> getPassword(){
        return this.password;
    }

    public LiveData<Integer> getValue(){
        return this.value;
    }

    public PasswordViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }
}
