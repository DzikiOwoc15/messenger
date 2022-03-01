package com.hfad.messenger2021.uiConversation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public ConversationFragment() {
        // Required empty public constructor
    }

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        BackEndViewModel backEndViewModel = new ViewModelProvider(getActivity()).get(BackEndViewModel.class);

        friendsNameTextView.setText(conversationObject.getFriendsName());
        if (conversationObject.getFriendsProfilePic() != null){
            friendsProfilePicImageView.setImageBitmap(conversationObject.getFriendsProfilePic());
        }
        else{
            friendsProfilePicImageView.setImageResource(R.drawable.ic_baseline_account_circle_24);
        }

        backEndViewModel.loadConversation(userObject.getId(), userObject.getApiKey(), conversationObject.getFriendsId()).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<JSONObject>() {
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(@NonNull Throwable e) {}
            @Override
            public void onComplete() {}
        });

        return root;
    }

    @Override
    public void onDestroy() {
        getRidOfDisposable.getRidOf(loadConversationDisposable);
        super.onDestroy();
    }
}