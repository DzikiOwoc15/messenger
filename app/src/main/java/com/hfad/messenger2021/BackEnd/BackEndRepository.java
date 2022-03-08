package com.hfad.messenger2021.BackEnd;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.hfad.messenger2021.Helpers.ConnectionToJSON;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;

public class BackEndRepository {
    public BackEndRepository(){

    }
    public Observable<Integer> createUser(String password, String email, String phoneNumber, String name, String surname){
        return  createUserASYNC(password, email, phoneNumber, name, surname);
    }

    private final String ipv4 = "192.168.1.100";
    private final int TIMEOUT_TIME = 6000;

    private Observable<Integer> createUserASYNC(String password, String email, String phoneNumber, String name, String surname){
        return Observable.fromCallable(() -> {
            URL url = new URL(String.format("http://%s:8080/api/createUser?password=%s&&email=%s&&phoneNumber=%s&&name=%s&&surname=%s",
                    ipv4,
                    password,
                    email,
                    phoneNumber,
                    name,
                    surname));
            try{
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setConnectTimeout(TIMEOUT_TIME);
                connection.connect();
                return  connection.getResponseCode();
            } catch (IOException ioException){
                Log.d("REPO", ioException.getMessage());
            }
            return 404;
        });
    }

    public Observable<JSONObject> loginUser(String emailOrPhone, String password, boolean viaEmail){
        return loginUserASYNC(emailOrPhone, password, viaEmail);
    }

    private Observable<JSONObject> loginUserASYNC(String emailOrPhone, String password, boolean viaEmail){
        URL url = null;
        if(viaEmail){
            try {
                url = new URL(String.format("http://%s:8080/api/loginUser?password=%s&email=%s",ipv4, password, emailOrPhone));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                url = new URL(String.format("http://%s:8080/api/loginUser?password=%s&phoneNumber=%s", ipv4, password, emailOrPhone));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        URL finalUrl = url;
        return Observable.fromCallable(() -> {
            if(finalUrl != null){
                try {
                    HttpURLConnection connection = (HttpURLConnection) finalUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(TIMEOUT_TIME);
                    connection.connect();
                    if(connection.getResponseCode() == 200){
                        return ConnectionToJSON.readAll(connection);
                    }
                } catch (SocketTimeoutException timeoutException){
                    return null;
                } catch (IOException e){
                    Log.d("REPO", e.getMessage());
                }
                return null;
            }
            return null;
        });
    }

    public @io.reactivex.rxjava3.annotations.NonNull Observable<Object> loadMainDataInterval(int userId, String apiKey){
        return loadMainDataIntervalASYNC(userId, apiKey);
    }

    private @io.reactivex.rxjava3.annotations.NonNull Observable<Object> loadMainDataIntervalASYNC(int userId, String apiKey){
        return  Observable.interval(0, 15, TimeUnit.SECONDS).flatMap(aLong -> loadMainDataASYNC(userId, apiKey));
    }

    public Observable<JSONObject> loadMainData(int userId, String apiKey){
        return loadMainDataASYNC(userId, apiKey);
    }

    private Observable<JSONObject> loadMainDataASYNC(int userId, String apiKey){
        return Observable.fromCallable(() -> {
            try {
                URL url = new URL(String.format("http://%s:8080/api/loadData?userId=%s&&apiKey=%s", ipv4, userId, apiKey));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(TIMEOUT_TIME);
                connection.setRequestMethod("GET");
                connection.connect();
                if(connection.getResponseCode() == 200){
                    return ConnectionToJSON.readAll(connection);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("REPO", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("REPO", e.getMessage());
            }
            return null;
        });
    }

    public Observable<Integer> sendFriendRequest(int userId, int friendsId ,String apiKey){
        return sendFriendRequestASYNC(userId, friendsId, apiKey);
    }

    private Observable<Integer> sendFriendRequestASYNC(int userId,int friendsId, String apiKey){
        return Observable.fromCallable(() -> {
            URL url = new URL(String.format("http://%s:8080/api/sendFriendRequest?userId=%s&friendsId=%s&apiKey=%s", ipv4, userId, friendsId, apiKey));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setConnectTimeout(TIMEOUT_TIME);
            connection.connect();
            return connection.getResponseCode();
        });
    }

    public Observable<Integer> answerFriendRequest(int userId, int requestId, String apiKey, boolean isAccepted){
        return answerFriendRequestASYNC(userId, requestId, apiKey, isAccepted);
    }

    private Observable<Integer> answerFriendRequestASYNC(int userId, int requestId, String apiKey, boolean isAccepted){
        return Observable.fromCallable(() -> {
            URL url = new URL(String.format("http://%s:8080/api/answerFriendRequest?userId=%s&requestId=%s&apiKey=%s&isAccepted=%s",
                    ipv4,
                    userId,
                    requestId,
                    apiKey,
                    isAccepted));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setConnectTimeout(TIMEOUT_TIME);
            connection.connect();
            return  connection.getResponseCode();
        });
    }

    public Observable<JSONObject> loadFriendRequests(int userId, String apiKey){
        return loadFriendRequestsASYNC(userId, apiKey);
    }

    private Observable<JSONObject> loadFriendRequestsASYNC(int userId, String apiKey){
        return Observable.fromCallable(() -> {
            URL url = new URL(String.format("http://%s:8080/api/loadFriendRequests?userId=%s&apiKey=%s", ipv4, userId, apiKey));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_TIME);
            connection.connect();
            if(connection.getResponseCode() == 200){
                return ConnectionToJSON.readAll(connection);
            }
            return null;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Observable<Integer> sendMessage(int userId, int conversationId , String apiKey, String message){
        return sendMessageASYNC(userId, conversationId, apiKey, stringEncoder(message));
    }

    private Observable<Integer> sendMessageASYNC(int userId, int conversationId , String apiKey, String message){
        return Observable.fromCallable(() -> {
            URL url = new URL(String.format("http://%s:8080/api/sendMessage?userId=%s&conversationId=%s&message=%s&apiKey=%s", ipv4, userId, conversationId, message, apiKey));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setConnectTimeout(TIMEOUT_TIME);
            connection.connect();
            return  connection.getResponseCode();
        });
    }

    public Observable<JSONObject> loadConversationInterval(int userId, String apiKey, int conversationId){
        return Observable.interval(0, 5, TimeUnit.SECONDS).flatMap(aLong -> loadConversationASYNC(userId, apiKey, conversationId));
    }

    public Observable<JSONObject> loadConversation(int userId, String apiKey, int conversationId){
        return loadConversationASYNC(userId, apiKey, conversationId);
    }

    private Observable<JSONObject> loadConversationASYNC(int userId, String apiKey, int conversationId){
        return Observable.fromCallable(() -> {
            URL url = new URL(String.format("http://%s:8080/api/loadConversation?userId=%s&apiKey=%s&conversationId=%s", ipv4, userId, apiKey, conversationId));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_TIME);
            connection.connect();
            if(connection.getResponseCode() == 200){
                return ConnectionToJSON.readAll(connection);
            }
            return null;
        });
    }

    public Observable<JSONObject> getNumberOfFriendRequests(int userId, String apiKey){
        return getNumberOfFriendRequestsASYNC(userId, apiKey);
    }

    private Observable<JSONObject> getNumberOfFriendRequestsASYNC(int userId, String apiKey){
        return Observable.fromCallable(() -> {
            URL url = new URL(String.format("http://%s:8080/api/loadNumberOfFriendRequests?userId=%s&apiKey=%s", ipv4, userId, apiKey));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_TIME);
            connection.connect();
            if (connection.getResponseCode() == 200){
                return ConnectionToJSON.readAll(connection);
            }
            return null;
        });
    }

    public Observable<JSONObject> loadUsersByString(String query, int userId, String apiKey){
        return loadUsersByStringASYNC(query, userId, apiKey);
    }

    private Observable<JSONObject> loadUsersByStringASYNC(String query, int userId, String apiKey){
        Log.d("REPO", String.format("Query: %s", query));
        return Observable.fromCallable(() -> {
            URL url = new URL(String.format("http://%s:8080/api/loadUsersByString?userId=%s&apiKey=%s&givenString=%s", ipv4, userId, apiKey, query));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_TIME);
            connection.connect();
            if (connection.getResponseCode() == 200){
                return ConnectionToJSON.readAll(connection);
            }
            return null;
        });
    }

    public Observable<Boolean> checkInternetConnection(){
        return checkInternetConnectionASYNC();
    }

    private Observable<Boolean> checkInternetConnectionASYNC(){
        return Observable.fromCallable(() -> {
            try{
                URL url = new URL("https://www.google.pl/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(TIMEOUT_TIME);
                connection.connect();
                return connection.getResponseCode() == 200;
            } catch (IOException e){
                Log.d("REPO", e.getMessage());
            }
            return false;
        });
    }

    private String stringEncoder(String text){
        try {
            return URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }

}
