package com.hfad.messenger2021.uiConversation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfad.messenger2021.BackEnd.BackEndViewModel;
import com.hfad.messenger2021.Helpers.getGson;
import com.hfad.messenger2021.Helpers.getRidOfDisposable;
import com.hfad.messenger2021.Objects.ConversationMessage;
import com.hfad.messenger2021.Objects.ConversationObject;
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


public class ConversationFragment extends Fragment {

    private static final String CONVERSATION_OBJECT_JSON = "param1";
    private static final String USER_JSON = "param2";

    private ConversationObject conversationObject;
    private User userObject;

    private Disposable loadConversationDisposable;
    private Disposable sendMessageDisposable;

    //Variables used to determine if recycler should scroll to the bottom
    private boolean wasScrolledForTheFirstTime = false;
    private int messageCount = 0;

    public ConversationFragment() {
        // Required empty public constructor
    }

    //User's and friend's data is passed in newInstance(param1, param2) in String format that can be converted to JSON
    public static ConversationFragment newInstance(String param1, String param2) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putString(CONVERSATION_OBJECT_JSON, param1);
        args.putString(USER_JSON, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String conversationObjectJSON = getArguments().getString(CONVERSATION_OBJECT_JSON);
            conversationObject = getGson.get().fromJson(conversationObjectJSON, ConversationObject.class);

            String userJSON = getArguments().getString(USER_JSON);
            userObject = getGson.get().fromJson(userJSON, User.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_conversation, container, false);
        TextView friendsNameTextView = root.findViewById(R.id.conversation_fragment_name);
        ImageView friendsProfilePicImageView = root.findViewById(R.id.conversation_fragment_profile_picture);
        EditText inputEditText = root.findViewById(R.id.conversation_fragment_input);
        ImageView sendMessageImageView = root.findViewById(R.id.conversation_fragment_send_message);

        RecyclerView recyclerView = root.findViewById(R.id.conversation_fragment_recycler);
        ConversationAdapter adapter = new ConversationAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //Make view go up with soft keyboard
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        BackEndViewModel backEndViewModel = new ViewModelProvider(getActivity()).get(BackEndViewModel.class);

        friendsNameTextView.setText(conversationObject.getConversationName());
        if (conversationObject.getConversationProfilePic() != null){
            friendsProfilePicImageView.setImageBitmap(conversationObject.getConversationProfilePic());
        }
        else{
            friendsProfilePicImageView.setImageResource(R.drawable.ic_baseline_account_circle_24);
        }

        backEndViewModel.loadConversationInterval(userObject.getId(), userObject.getApiKey(), conversationObject.getConversationId()).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {loadConversationDisposable = d;}
            @Override
            public void onNext(@NonNull JSONObject jsonObject) {
                try {
                    List<ConversationMessage> messageList = new ArrayList<>();
                    JSONArray conversation = jsonObject.getJSONArray("conversation");
                    for (int i = 0; i < conversation.length(); i++){
                        Integer messageId = conversation.getJSONObject(i).getInt("message_id");
                        int authorsId = conversation.getJSONObject(i).getInt("authors_id");
                        String message = conversation.getJSONObject(i).getString("message");
                        String messageDate = conversation.getJSONObject(i).getString("message_date");

                        messageList.add(new ConversationMessage(message, (userObject.getId() == authorsId)));
                    }
                    adapter.setMessageList(messageList);
                    adapter.notifyDataSetChanged();

                    //Show new messages when conversation is scrolled all the way down (last 2 messages are at the bottom) and new message is received
                    //                  OR
                    //Scroll to bottom for the first time when Fragment is created
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    if(((messageList.size() - 1 - lastVisibleItem) < 2 && messageCount != messageList.size()) || !wasScrolledForTheFirstTime){
                        Log.d("Scroll", "Scrolled");
                        wasScrolledForTheFirstTime = true;
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }
                    messageCount = messageList.size();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(@NonNull Throwable e) {}
            @Override
            public void onComplete() {}
        });

        sendMessageImageView.setOnClickListener(view -> {
            if(inputEditText.getText().toString().length() != 0){
                backEndViewModel.sendMessage(userObject.getId(), conversationObject.getConversationId(), userObject.getApiKey(), inputEditText.getText().toString()).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {sendMessageDisposable = d;}
                    @Override
                    public void onNext(@NonNull Integer integer) {
                        if(integer == 200){
                            //Add new message to the list
                            adapter.addMessage(new ConversationMessage(inputEditText.getText().toString(), true));
                            //Do not wait for the recycler to refresh itself to scroll to the bottom, do it now
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                            inputEditText.setText("");
                        }
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {}
                    @Override
                    public void onComplete() {}
                });
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        getRidOfDisposable.getRidOf(loadConversationDisposable);
        getRidOfDisposable.getRidOf(sendMessageDisposable);
        super.onDestroyView();
    }
}