package com.hfad.messenger2021.LocalDatabase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hfad.messenger2021.Objects.User;

public class LocalDatabaseViewModel extends AndroidViewModel {
    LocalDatabaseRepository repository;

    public LocalDatabaseViewModel(@NonNull Application application) {
        super(application);
        repository = new LocalDatabaseRepository(application);
    }

    public void setUser(Integer integer, String apiKey){
        repository.insertUser(integer, apiKey);
    }

    public LiveData<User> getUser(){
        return repository.getUser();
    }
}
