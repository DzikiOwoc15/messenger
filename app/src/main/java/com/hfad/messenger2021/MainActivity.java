package com.hfad.messenger2021;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.hfad.messenger2021.LocalDatabase.LocalDatabaseViewModel;
import com.hfad.messenger2021.Objects.User;
import com.hfad.messenger2021.uiLogin.CreateAccountFragment;
import com.hfad.messenger2021.uiMain.MainScreenFragment;

public class MainActivity extends AppCompatActivity {
    private User INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalDatabaseViewModel localDatabaseViewModel = new ViewModelProvider(this).get(LocalDatabaseViewModel.class);
        localDatabaseViewModel.getUser().observe(this, user -> {
            INSTANCE = user;
            if(INSTANCE == null){
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, CreateAccountFragment.class, null).commit();
            }
            else{
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, MainScreenFragment.class, null).commit();
            }
        });

    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.getBackStackEntryCount() > 0){
            fragmentManager.popBackStack();
        }
        else{
            super.onBackPressed();
        }
    }
}