package com.hfad.messenger2021.uiConversation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hfad.messenger2021.R;


public class ConversationFragment extends Fragment {

    private static final String CONVERSATION_OBJECT_JSON = "param1";

    private String conversationObjectJSON;

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
            conversationObjectJSON = getArguments().getString(CONVERSATION_OBJECT_JSON);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }
}