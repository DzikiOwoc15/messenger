package com.hfad.messenger2021.uiMain;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hfad.messenger2021.BackEnd.BackEndViewModel;
import com.hfad.messenger2021.Helpers.StringFormatter;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

//TODO FIX INCORRECT LAST MESSAGE

public class MainScreenFragment extends Fragment {

    private Disposable friendRequestsDisposable;
    private Disposable loadDataDisposable;
    private Disposable emptyClickDisposable;
    private Disposable friendCLickDisposable;
    private Disposable checkInternetConnectionDisposable;
    private ImageView profilePic = null;


    public MainScreenFragment() {
        // Required empty public constructor
    }

    public static MainScreenFragment newInstance(String param1, String param2) {
        MainScreenFragment fragment = new MainScreenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    ActivityResultLauncher<Intent> cropLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getData() != null){
            Bundle extras = result.getData().getExtras();
            Bitmap pic = extras.getParcelable("data");
            profilePic.setImageBitmap(pic);
        }
    });


    ActivityResultLauncher<Intent> pictureSelectionLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK){
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(result.getData().getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                Uri picUri = result.getData().getData();
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(picUri, "image/*");
                //set crop properties
                cropIntent.putExtra("crop", "true");
                //indicate aspect of desired crop
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                //indicate output X and Y
                cropIntent.putExtra("outputX", 256);
                cropIntent.putExtra("outputY", 256);
                //retrieve data on return
                cropIntent.putExtra("return-data", true);

                cropLauncher.launch(cropIntent);
                //profilePic.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    });

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
        profilePic = root.findViewById(R.id.main_profile_pic);
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
            backEndViewModel.loadMainDataInterval(user.getId(), user.getApiKey()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {loadDataDisposable = d;}
                @Override
                public void onNext(@NonNull Object usersJSON) {
                    List<ConversationObject> conversationObjList = new ArrayList<>();
                    try {
                        JSONArray array = ((JSONObject)usersJSON).getJSONArray("conversations");
                        for(int i = 0; i < array.length(); i++){
                            int id = array.getJSONObject(i).getInt("id");
                            JSONArray conversationUsersArray = array.getJSONObject(i).getJSONArray("users");
                            String nameCombined = "";
                            StringBuilder stringBuilder = new StringBuilder(nameCombined);
                            for (int j = 0; j < conversationUsersArray.length(); j++){
                                String conversationUserName = conversationUsersArray.getJSONObject(i).getString("name");
                                String conversationUserSurname = conversationUsersArray.getJSONObject(i).getString("surname");
                                String conversationUserId = conversationUsersArray.getJSONObject(i).getString("id");

                                stringBuilder.append(String.format("%s %s", conversationUserName, conversationUserSurname));
                            }
                            String timestamp = array.getJSONObject(i).getString("last_message_timestamp");
                            String lastMessage = array.getJSONObject(i).getString("last_message");

                            ConversationObject conversation = new ConversationObject(nameCombined, id);

                            if (timestamp.equals("null")){
                                conversation.setGetConversationLastMessageTimeStamp("");
                            }
                            else{
                                conversation.setGetConversationLastMessageTimeStamp(StringFormatter.dateFormat(timestamp));
                            }

                            if (lastMessage.equals("null")){
                                conversation.setConversationLastMessage("");
                            }
                            else{
                                conversation.setConversationLastMessage(StringFormatter.nameFormat(lastMessage, "TEMPORARY", false, getString(R.string.you)));
                                //TODO UPDATE RETURN SCHEMA INCLUDE LAST_MESSAGE_AUTHORS_ID
                            }

                        }
                        recyclerAdapter.setConversationList(conversationObjList);
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

        friendRequestsImageView.setOnClickListener(view -> getParentFragmentManager().beginTransaction().replace(R.id.main_fragment_container, FriendRequestsFragment.class, null).addToBackStack("MainScreen").commit());

        //Main profile pic onClickListener
        profilePic.setOnClickListener(view -> {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

            pictureSelectionLauncher.launch(pickIntent);
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

    @Override
    public void onDestroyView() {
        getRidOfDisposable.getRidOf(loadDataDisposable);
        super.onDestroyView();
    }
}