package com.hfad.messenger2021.uiMain;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hfad.messenger2021.BackEnd.BackEndViewModel;
import com.hfad.messenger2021.Helpers.getGson;
import com.hfad.messenger2021.LocalDatabase.LocalDatabaseViewModel;
import com.hfad.messenger2021.Objects.ConversationObject;
import com.hfad.messenger2021.R;
import com.hfad.messenger2021.Helpers.getRidOfDisposable;
import com.hfad.messenger2021.uiConversation.ConversationFragment;
import com.hfad.messenger2021.uiFriendRequests.FriendRequestsFragment;
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


public class MainScreenFragment extends Fragment {

    private Disposable friendRequestsDisposable;
    private Disposable loadDataDisposable;
    private Disposable emptyClickDisposable;
    private Disposable friendCLickDisposable;
    private Disposable checkInternetConnectionDisposable;


    public MainScreenFragment() {
        // Required empty public constructor
    }

    public static MainScreenFragment newInstance(String param1, String param2) {
        MainScreenFragment fragment = new MainScreenFragment();
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
        View root = inflater.inflate(R.layout.fragment_main_screen, container, false);
        ImageView friendRequestsImageView = root.findViewById(R.id.main_friend_requests);
        RelativeLayout friendRequestsWrapper = root.findViewById(R.id.main_friend_requests_wrapper);
        TextView friendRequestsBadge = friendRequestsWrapper.findViewById(R.id.main_friend_requests_badge);

        RecyclerView recyclerView = root.findViewById(R.id.main_recycler);
        MainScreenAdapter recyclerAdapter = new MainScreenAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);

        LocalDatabaseViewModel localDatabaseViewModel = new ViewModelProvider(getActivity()).get(LocalDatabaseViewModel.class);
        BackEndViewModel backEndViewModel = new ViewModelProvider(getActivity()).get(BackEndViewModel.class);

        //Check if internet connection is working
        backEndViewModel.isInternetConnectionWorking().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {checkInternetConnectionDisposable = d;}
            @Override
            public void onNext(@NonNull Boolean isConnectionWorking) {
                recyclerAdapter.setIsConnectionWorking(isConnectionWorking);
                recyclerAdapter.notifyDataSetChanged();
            }
            @Override
            public void onError(@NonNull Throwable e) {}
            @Override
            public void onComplete() {}
        });

        localDatabaseViewModel.getUser().observe(getViewLifecycleOwner(), user -> {

            //Load data for the main recycler
            backEndViewModel.loadData(user.getId(), user.getApiKey()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<JSONObject>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    loadDataDisposable = d;}
                @Override
                public void onNext(@NonNull JSONObject usersJSON) {
                    List<String> friendsList = new ArrayList<>();
                    List<String> lastMessageTimestampList = new ArrayList<>();
                    List<String> lastMessageList = new ArrayList<>();
                    List<Integer> idList = new ArrayList<>();
                    try {
                        JSONArray array = usersJSON.getJSONArray("friends");
                        for(int i = 0; i < array.length(); i++){
                            Integer id = array.getJSONObject(i).getInt("id");
                            String name = array.getJSONObject(i).getString("name");
                            String surname = array.getJSONObject(i).getString("surname");
                            String timestamp = array.getJSONObject(i).getString("last_message_timestamp");
                            String lastMessage = array.getJSONObject(i).getString("last_message");

                            friendsList.add(String.format("%s %s", name, surname));

                            if (timestamp.equals("null")){
                                lastMessageTimestampList.add("");
                            }
                            else{
                                lastMessageTimestampList.add(timestamp);
                            }

                            if (lastMessage.equals("null")){
                                lastMessageList.add("");
                            }
                            else{
                                lastMessageList.add(lastMessage);
                            }

                        }
                        Log.d("MainScreenFragment", String.format("Conversations loaded: %d", friendsList.size()));
                        recyclerAdapter.setIdList(idList);
                        recyclerAdapter.setUsernameList(friendsList);
                        recyclerAdapter.setLastMessageList(lastMessageList);
                        recyclerAdapter.setLastMessageTimestampList(lastMessageTimestampList);
                        recyclerAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                @Override
                public void onError(@NonNull Throwable e) {}
                @Override
                public void onComplete() {}

            });

            //Number of friend requests received (displayed on a badge)
            backEndViewModel.getNumberOfFriendRequests(user.getId(), user.getApiKey()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<JSONObject>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {friendRequestsDisposable = d;}
                @Override
                public void onNext(@NonNull JSONObject jsonObject) {
                    try {
                        JSONArray array = jsonObject.getJSONArray("result");
                        int numberOfFriendRequests = array.getInt(0);
                        if (numberOfFriendRequests != 0){
                            friendRequestsBadge.setVisibility(View.VISIBLE);
                            friendRequestsBadge.setText(String.valueOf(numberOfFriendRequests));
                        }
                        else{
                            friendRequestsBadge.setVisibility(View.INVISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(@NonNull Throwable e) {}
                @Override
                public void onComplete() {}
            });

            //On friend click listener (open conversation with that friend)
            recyclerAdapter.getOnFriendClick().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ConversationObject>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {friendCLickDisposable = d;}
                @Override
                public void onNext(@NonNull ConversationObject object) {
                    String conversationObjectJSON = getGson.get().toJson(object);
                    String userJSON = getGson.get().toJson(user);
                    Fragment conversation_fragment = ConversationFragment.newInstance(conversationObjectJSON, userJSON);
                    getParentFragmentManager().beginTransaction().replace(R.id.main_fragment_container, conversation_fragment, null).addToBackStack("MainScreen").commit();
                }
                @Override
                public void onError(@NonNull Throwable e) {}
                @Override
                public void onComplete() {}
            });

        });

        //If recycler view is empty a on click listener is set
        recyclerAdapter.getClick().subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {emptyClickDisposable = d;}
            @Override
            public void onNext(@NonNull String click) {
                getParentFragmentManager().beginTransaction().replace(R.id.main_fragment_container, FragmentSearchForFriends.class, null).addToBackStack("MainScreen").commit();
            }
            @Override
            public void onError(@NonNull Throwable e) {}
            @Override
            public void onComplete() {}
        });

        friendRequestsImageView.setOnClickListener(view -> {
            getParentFragmentManager().beginTransaction().replace(R.id.main_fragment_container, FriendRequestsFragment.class, null).addToBackStack("MainScreen").commit();
        });

        return root;
    }

    @Override
    public void onDestroy() {
        getRidOfDisposable.getRidOf(emptyClickDisposable);
        getRidOfDisposable.getRidOf(friendCLickDisposable);
        getRidOfDisposable.getRidOf(friendRequestsDisposable);
        getRidOfDisposable.getRidOf(loadDataDisposable);
        getRidOfDisposable.getRidOf(checkInternetConnectionDisposable);
        super.onDestroy();
    }



}