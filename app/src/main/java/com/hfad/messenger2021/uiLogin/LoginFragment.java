package com.hfad.messenger2021.uiLogin;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.hfad.messenger2021.BackEnd.BackEndViewModel;
import com.hfad.messenger2021.LocalDatabase.LocalDatabaseViewModel;
import com.hfad.messenger2021.Objects.ConnectionToJSON;
import com.hfad.messenger2021.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class LoginFragment extends Fragment {

    private Disposable loginDisposable;
    private LocalDatabaseViewModel localDatabaseViewModel;


    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        BackEndViewModel backEndViewModel = new ViewModelProvider(requireActivity()).get(BackEndViewModel.class);
        localDatabaseViewModel = new ViewModelProvider(requireActivity()).get(LocalDatabaseViewModel.class);
        EditText emailTextView = root.findViewById(R.id.login_email);
        EditText passwordTextView = root.findViewById(R.id.login_password);
        EditText phoneTextView = root.findViewById(R.id.login_phone);
        Button loginButton = root.findViewById(R.id.login_button);
        loginButton.setOnClickListener(view -> {
            if(emailTextView.getText().length() != 0 && passwordTextView.getText().length() != 0){
                backEndViewModel.loginUser(passwordTextView.getText().toString(), emailTextView.getText().toString(), true).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        loginDisposable = d;
                    }
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onNext(@NonNull JSONObject jsonObject) {
                        readLoginResponse(jsonObject);
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {}
                    @Override
                    public void onComplete() {}
                });
            }
           else if(phoneTextView.getText().length() != 0 && passwordTextView.length() != 0){
                backEndViewModel.loginUser(passwordTextView.getText().toString(), phoneTextView.getText().toString(), false).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        loginDisposable = d;
                    }
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onNext(@NonNull JSONObject jsonObject) {
                        readLoginResponse(jsonObject);
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {}
                    @Override
                    public void onComplete() {}
                });
            }
           else{
                if(emailTextView.getText().length() == 0){
                    emailTextView.setError("Enter valid email address");
                }
                if(phoneTextView.getText().length() == 0){
                    phoneTextView.setError("Enter valid phone number");
                }
                if(passwordTextView.getText().length() == 0){
                    passwordTextView.setError("Enter valid password");
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroy() {
        if(loginDisposable != null && !loginDisposable.isDisposed()){
            loginDisposable.dispose();
        }
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void readLoginResponse(JSONObject json){
        try {
            localDatabaseViewModel.setUser(json.getInt("id"), json.getString("api_key"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}