package com.hfad.messenger2021.uiConversation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.messenger2021.Objects.ConversationMessage;
import com.hfad.messenger2021.R;

import java.util.ArrayList;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<ConversationMessage> messageList = new ArrayList<>();

    private final int MESSAGE_VIEW_TYPE_SENT_BY_ME = 0;
    private final int MESSAGE_VIEW_TYPE_RECEIVED = 1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MESSAGE_VIEW_TYPE_SENT_BY_ME){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_message, parent, false);
            return new messageViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_message_received, parent, false);
            return new messageReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof messageViewHolder){
            ((messageViewHolder) holder).messageTextView.setText(messageList.get(position).getMessage());
        }
        else if (holder instanceof  messageReceivedViewHolder){
            ((messageReceivedViewHolder) holder).messageTextView.setText(messageList.get(position).getMessage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messageList.get(position).wasSentByMe()){
            return  MESSAGE_VIEW_TYPE_SENT_BY_ME;
        }
        else{
            return MESSAGE_VIEW_TYPE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {return messageList.size();}

    public void setMessageList(List<ConversationMessage> messageList) {this.messageList = messageList;}

    public static class messageReceivedViewHolder extends RecyclerView.ViewHolder{
        public final TextView messageTextView;

        public messageReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.text_message_received);
        }
    }

    public static class messageViewHolder extends RecyclerView.ViewHolder{
        public final TextView messageTextView;

        public messageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.text_message);
        }
    }
}
