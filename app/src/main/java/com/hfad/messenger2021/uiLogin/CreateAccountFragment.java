package com.hfad.messenger2021.uiLogin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hfad.messenger2021.BackEnd.BackEndViewModel;
import com.hfad.messenger2021.LocalDatabase.LocalDatabaseViewModel;
import com.hfad.messenger2021.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CreateAccountFragment extends Fragment {

    private EditText emailTextView;
    private EditText passwordTextView;
    private Button createAccountButton;
    private BackEndViewModel backEndViewModel;
    private EditText phoneTextView;
    private Button loginButton;
    private EditText nameTextView;
    private EditText surnameTextView;
    private LocalDatabaseViewModel localDatabaseViewModel;
    private Disposable loginDisposable;

    public CreateAccountFragment() {
        // Required empty public constructor
    }

    public static CreateAccountFragment newInstance(String param1, String param2) {
        CreateAccountFragment fragment = new CreateAccountFragment();
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
        View root = inflater.inflate(R.layout.fragment_create_user, container, false);
        emailTextView = root.findViewById(R.id.create_account_email);
        passwordTextView = root.findViewById(R.id.create_account_password);
        createAccountButton = root.findViewById(R.id.create_account_button);
        phoneTextView = root.findViewById(R.id.create_account_phone);
        loginButton = root.findViewById(R.id.go_to_logging_screen_button);
        nameTextView = root.findViewById(R.id.create_account_name);
        surnameTextView = root.findViewById(R.id.create_account_surname);
        backEndViewModel = new ViewModelProvider(requireActivity()).get(BackEndViewModel.class);
        localDatabaseViewModel = new ViewModelProvider(requireActivity()).get(LocalDatabaseViewModel.class);

        emailTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().contains("@")){
                    emailTextView.setError("Invalid Email Address!");
                }
                else{
                    emailTextView.setError(null);
                }
            }
        });

        passwordTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() < 5){
                    passwordTextView.setError("Password must be at least 5 characters long");
                }
                else{
                    passwordTextView.setError(null);
                }
            }
        });

        phoneTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() < 9){
                    phoneTextView.setError("Enter valid phone number");
                }
                else{
                    phoneTextView.setError(null);
                }
            }
        });

        nameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() == 0){
                    nameTextView.setError("Enter your name");
                }
                else{
                    nameTextView.setError(null);
                }
            }
        });

        surnameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() == 0){
                    surnameTextView.setError("Enter your surname");
                }
                else{
                    surnameTextView.setError(null);
                }
            }
        });


        createAccountButton.setOnClickListener(view -> {
                if(passwordTextView.getError() == null && emailTextView.getError() == null && phoneTextView.getError() == null && nameTextView.getError() == null && surnameTextView.getError() == null && nameTextView.getText().toString().length() != 0 && surnameTextView.getText().toString().length() != 0 && phoneTextView.getText().toString().length() != 0 && !passwordTextView.getText().toString().equals("") && !emailTextView.getText().toString().equals("")){
                    backEndViewModel.createUser(passwordTextView.getText().toString(), emailTextView.getText().toString(), phoneTextView.getText().toString(), nameTextView.getText().toString(), surnameTextView.getText().toString()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {}
                        @Override
                        public void onNext(@NonNull Integer integer) {
                            if(integer == 200){

                                Toast.makeText(getContext(), "Account created", Toast.LENGTH_SHORT).show();
                                backEndViewModel.loginUser(passwordTextView.getText().toString(), emailTextView.getText().toString(), true).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<JSONObject>() {
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d) {loginDisposable = d;}
                                    @Override
                                    public void onNext(@NonNull JSONObject jsonObject) {
                                        try {
                                            localDatabaseViewModel.setUser(jsonObject.getInt("id"), jsonObject.getString("api_key"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    @Override
                                    public void onError(@NonNull Throwable e) {}
                                    @Override
                                    public void onComplete() {}
                                });


                            }
                            else if (integer == 409){
                                Toast.makeText(getContext(), "Email or phone is already used", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d("CreateAccountFragment", e.toString());
                        }
                        @Override
                        public void onComplete() {}
                    });
                }
                else{
                    if(passwordTextView.getText().toString().length() < 5){
                        passwordTextView.setError("Password must be at least 5 characters long");
                    }
                    if(phoneTextView.getText().toString().length() < 9){
                        phoneTextView.setError("Enter valid phone number");
                    }
                    if(!emailTextView.getText().toString().contains("@")){
                        emailTextView.setError("Invalid Email Address!");
                    }
                }
        });

        loginButton.setOnClickListener(view -> {
            Fragment fragment = new LoginFragment();
            requireActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_fragment_container, fragment).addToBackStack("Login").commit();
        });
        return root;
    }

    @Override
    public void onDestroy() {
        if (loginDisposable != null && !loginDisposable.isDisposed()){
            loginDisposable.dispose();
        }
        super.onDestroy();
    }
}