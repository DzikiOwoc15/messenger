package com.hfad.messenger2021.uiConversation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfad.messenger2021.Helpers.getGson;
import com.hfad.messenger2021.Objects.ConversationObject;
import com.hfad.messenger2021.R;


public class ConversationFragment extends Fragment {

    private static final String CONVERSATION_OBJECT_JSON = "param1";

    private ConversationObject conversationObject;

    public ConversationFragment() {
        // Required empty public constructor
    }

    public static ConversationFragment newInstance(String param1) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putString(CONVERSATION_OBJECT_JSON, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String conversationObjectJSON = getArguments().getString(CONVERSATION_OBJECT_JSON);
            conversationObject = getGson.get().fromJson(conversationObjectJSON, ConversationObject.class);
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

        friendsNameTextView.setText(conversationObject.getFriendsName());
        if (conversationObject.getFriendsProfilePic() != null){
            friendsProfilePicImageView.setImageBitmap(conversationObject.getFriendsProfilePic());
        }
        else{
            friendsProfilePicImageView.setImageResource(R.drawable.ic_baseline_account_circle_24);
        }

        return root;
    }
}