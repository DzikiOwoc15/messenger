package com.hfad.messenger2021.BackEnd;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.json.JSONObject;

import io.reactivex.rxjava3.core.Observable;

public class BackEndViewModel extends AndroidViewModel {
    BackEndRepository backEndRepository;

    public BackEndViewModel(@NonNull Application application) {
        super(application);
        backEndRepository = new BackEndRepository();
    }

    public Observable<Integer> createUser(String password, String email, String phoneNumber, String name, String surname){
        return backEndRepository.createUser(password, email, phoneNumber, name, surname);
    }

    public Observable<JSONObject> loginUser(String password, String email, boolean viaEmail){
        return  backEndRepository.loginUser(email, password,viaEmail);
    }

    public Observable<JSONObject> loadData(int userId, String apiKey){
        return backEndRepository.loadMainData(userId, apiKey);
    }

    public Observable<Integer> sendFriendRequest(int userId, int friendsId, String apiKey){
        return backEndRepository.sendFriendRequest(userId, friendsId, apiKey);
    }

    public Observable<Integer> answerFriendRequest(int userId, int requestId, String apiKey, boolean isAccepted){
        return backEndRepository.answerFriendRequest(userId, requestId, apiKey, isAccepted);
    }

    public Observable<JSONObject> loadFriendRequests(int userId, String apiKey){
        return backEndRepository.loadFriendRequests(userId, apiKey);
    }

    public Observable<Integer> sendMessage(int userId, int friendsId, String apiKey, String message){
        return backEndRepository.sendMessage(userId, friendsId, apiKey, message);
    }

    public Observable<JSONObject> loadConversation(int userId, String apiKey, int friendsId){
        return backEndRepository.loadConversation(userId, apiKey, friendsId);
    }

    public Observable<JSONObject> getNumberOfFriendRequests(int userId, String apiKey){
        return backEndRepository.getNumberOfFriendRequests(userId, apiKey);
    }

    public Observable<JSONObject> loadUsersByString(String query, int userId, String apiKey){
        return backEndRepository.loadUsersByString(query, userId, apiKey);
    }

    public Observable<Boolean> isInternetConnectionWorking(){
        return backEndRepository.checkInternetConnection();
    }

    public @io.reactivex.rxjava3.annotations.NonNull Observable<Object> loadMainDataInterval(int userId, String apiKey){
        return backEndRepository.loadMainDataInterval(userId, apiKey);
    }

    public Observable<JSONObject> loadConversationInterval(int userId, String apiKey, int friendsId){
        return backEndRepository.loadConversationInterval(userId, apiKey, friendsId);
    }

}
