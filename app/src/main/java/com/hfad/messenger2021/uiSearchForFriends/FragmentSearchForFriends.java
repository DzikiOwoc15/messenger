package com.hfad.messenger2021.uiSearchForFriends;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hfad.messenger2021.BackEnd.BackEndViewModel;
import com.hfad.messenger2021.LocalDatabase.LocalDatabaseViewModel;
import com.hfad.messenger2021.Objects.User;
import com.hfad.messenger2021.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentSearchForFriends extends Fragment {

    private Disposable usersDisposable;
    private Disposable clickDisposable;
    private Disposable requestDisposable;
    private final String TAG = "FragmentSearchForFriend";

    public FragmentSearchForFriends() {
        // Required empty public constructor
    }

    public static FragmentSearchForFriends newInstance(String param1, String param2) {
        return new FragmentSearchForFriends();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_search_for_friends, container, false);
        SearchView searchView = root.findViewById(R.id.search_for_friends_search_view);

        TextView textView = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        textView.setTextColor(Color.WHITE);

        RecyclerView recyclerView = root.findViewById(R.id.search_for_friends_recycler);
        SearchForFriendsAdapter adapter = new SearchForFriendsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        BackEndViewModel backEndViewModel = new ViewModelProvider(getActivity()).get(BackEndViewModel.class);
        LocalDatabaseViewModel localDatabaseViewModel = new ViewModelProvider(getActivity()).get(LocalDatabaseViewModel.class);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() != 0){

                    Toast.makeText(getContext(), "Change: " + newText, Toast.LENGTH_SHORT).show();
                    localDatabaseViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
                        backEndViewModel.loadUsersByString(newText, user.getId(), user.getApiKey()).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<JSONObject>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {usersDisposable = d;}
                            @Override
                            public void onNext(@NonNull JSONObject jsonObject) {
                                try {
                                    JSONArray usersArray = jsonObject.getJSONArray("users");
                                    List<String> nameList = new ArrayList<>();
                                    List<Integer> idList = new ArrayList<>();

                                    for (int i = 0; i < usersArray.length(); i++){
                                        int id = usersArray.getJSONObject(i).getInt("id");
                                        String name = usersArray.getJSONObject(i).getString("name");

                                        nameList.add(name);
                                        idList.add(id);
                                    }
                                    adapter.setIdList(idList);
                                    adapter.setNameList(nameList);
                                    adapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.d(TAG, e.toString());
                            }
                            @Override
                            public void onComplete() {}
                        });
                    });

                }
                return false;
            }
        });

        localDatabaseViewModel.getUser().observe(getViewLifecycleOwner(), user -> {

            //Recycler on click
            adapter.getClickPublishSubject().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {clickDisposable = d;}
                @Override
                public void onNext(@NonNull Integer friendId) {

                    //Send friend request on click
                    backEndViewModel.sendFriendRequest(user.getId(), friendId, user.getApiKey()).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<Integer>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {requestDisposable = d;}
                        @Override
                        public void onNext(@NonNull Integer responseCode) {
                            if(responseCode == 200){
                                Toast.makeText(getContext(), "Request has been send", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d(TAG, e.toString());
                        }
                        @Override
                        public void onComplete() {}
                    });
                }
                @Override
                public void onError(@NonNull Throwable e) {
                    Log.d(TAG, e.toString());
                }
                @Override
                public void onComplete() {}
            });

        });

        return root;
    }

    @Override
    public void onDestroy() {
        if(usersDisposable != null && !usersDisposable.isDisposed()){
            usersDisposable.dispose();
        }
        if(clickDisposable != null && !clickDisposable.isDisposed()){
            clickDisposable.dispose();
        }
        if(requestDisposable != null && !requestDisposable.isDisposed()){
            requestDisposable.dispose();
        }
        super.onDestroy();
    }
}