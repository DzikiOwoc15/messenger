package com.hfad.messenger2021.uiFriendRequests;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hfad.messenger2021.BackEnd.BackEndViewModel;
import com.hfad.messenger2021.Helpers.getRidOfDisposable;
import com.hfad.messenger2021.LocalDatabase.LocalDatabaseViewModel;
import com.hfad.messenger2021.Objects.RequestResponse;
import com.hfad.messenger2021.R;
import com.hfad.messenger2021.uiSearchForFriends.FragmentSearchForFriends;

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


public class FriendRequestsFragment extends Fragment {

    private final String TAG = "FriendRequestsFragment";
    private Disposable emptyClickDisposable;
    private Disposable clickDisposable;
    private Disposable loadRequestsDisposable;


    public FriendRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_friend_requests, container, false);

        RecyclerView recyclerview = root.findViewById(R.id.friend_requests_recycler);
        FriendRequestsAdapter adapter = new FriendRequestsAdapter();
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview.setAdapter(adapter);

        //Empty recycler click
        adapter.getClick().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {emptyClickDisposable = d;}
            @Override
            public void onNext(@NonNull String s) {
                getParentFragmentManager().beginTransaction().replace(R.id.main_fragment_container, FragmentSearchForFriends.class, null).addToBackStack("FriendRequests").commit();
            }
            @Override
            public void onError(@NonNull Throwable e) {}
            @Override
            public void onComplete() {}
        });

        BackEndViewModel backEndViewModel = new ViewModelProvider(getActivity()).get(BackEndViewModel.class);
        LocalDatabaseViewModel localDatabaseViewModel = new ViewModelProvider(getActivity()).get(LocalDatabaseViewModel.class);

        //Load requests data
        localDatabaseViewModel.getUser().observe(getActivity(), user -> {

            backEndViewModel.loadFriendRequests(user.getId(), user.getApiKey()).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<JSONObject>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {loadRequestsDisposable = d;}
                @Override
                public void onNext(@NonNull JSONObject jsonObject) {
                    try {
                        JSONArray requests = jsonObject.getJSONArray("requests");
                        List<Integer> userIdList = new ArrayList<>();
                        List<String> nameList = new ArrayList<>();
                        List<Integer> relationIdList = new ArrayList<>();

                        for (int i = 0; i < requests.length(); i++){
                            int relationId = requests.getJSONObject(i).getInt("relation_id");
                            int userId = requests.getJSONObject(i).getInt("user_id");
                            String name = requests.getJSONObject(i).getString("name");

                            userIdList.add(userId);
                            nameList.add(name);
                            relationIdList.add(relationId);
                        }
                        adapter.setNameList(nameList);
                        adapter.setUserIdList(userIdList);
                        adapter.setRelationId(relationIdList);
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

            //Accept or decline click adapter
            adapter.getRequestClickPublisher().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<RequestResponse>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {clickDisposable = d;}
                @Override
                public void onNext(@NonNull RequestResponse userResponse) {

                    backEndViewModel.answerFriendRequest(user.getId(), userResponse.getRelationId(), user.getApiKey(), userResponse.isAccepted()).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<Integer>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {}
                        @Override
                        public void onNext(@NonNull Integer responseCode) {
                            if(responseCode == 200){
                                Toast.makeText(getActivity(), "Response has been send", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onError(@NonNull Throwable e) {}
                        @Override
                        public void onComplete() {}
                    });

                }
                @Override
                public void onError(@NonNull Throwable e) {}
                @Override
                public void onComplete() {}
            });
        });

        return root;
    }

    @Override
    public void onDestroy() {
        getRidOfDisposable.getRidOf(clickDisposable);
        getRidOfDisposable.getRidOf(emptyClickDisposable);
        getRidOfDisposable.getRidOf(loadRequestsDisposable);
        super.onDestroy();
    }
}