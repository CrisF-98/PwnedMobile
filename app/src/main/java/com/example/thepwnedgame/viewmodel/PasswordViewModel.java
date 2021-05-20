package com.example.thepwnedgame.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class PasswordViewModel extends AndroidViewModel {

    private final Application application;
    private final MutableLiveData<String> firstPassword = new MutableLiveData<>();
    private final MutableLiveData<String> secondPassword = new MutableLiveData<>();
    private final MutableLiveData<Integer> value = new MutableLiveData<>();
    private final MutableLiveData<String> value2 = new MutableLiveData<>();

    public void setFirstPassword(String password){
        this.firstPassword.postValue(password);
    }

    public void setSecondPassword(String password) { this.secondPassword.postValue(password); }

    public void setValue(Integer value){
        this.value.postValue(value);
    }

    public void setValue2(String value) {
        this.value2.setValue(value);
    }

    public LiveData<String> getFirstPassword(){
        return this.firstPassword;
    }

    public LiveData<String> getSecondPassword() {
        return this.secondPassword;
    }

    public LiveData<Integer> getValue(){
        return this.value;
    }

    public LiveData<String> getValue2() {
        return this.value2;
    }

    public PasswordViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }
}
