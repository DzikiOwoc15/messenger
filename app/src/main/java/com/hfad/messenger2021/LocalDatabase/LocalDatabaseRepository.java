package com.hfad.messenger2021.LocalDatabase;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hfad.messenger2021.Objects.User;

import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LocalDatabaseRepository {
    private LocalDatabaseDao dao;
    public LocalDatabaseRepository(Application application){
        AppDatabase database = AppDatabase.buildDatabase(application);
        dao = database.DAO();
    }

    public void insertUser(Integer integer, String apiKey){
        Observable.fromCallable((Callable<Void>) () -> {
            dao.insertUser(new User(integer, apiKey));
            return null;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Void>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {}
            @Override
            public void onNext(@NonNull Void aVoid) {}
            @Override
            public void onError(@NonNull Throwable e) {}
            @Override
            public void onComplete() {}
        });
    }

    public LiveData<User> getUser(){
        return dao.getUser();
    }
}
