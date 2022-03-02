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
    private final int MESSAGE_SENT_TOP = 100;
    private final int MESSAGE_SENT_MIDDLE = 101;
    private final int MESSAGE_SENT_BOTTOM = 102;
    private final int MESSAGE_VIEW_TYPE_RECEIVED = 1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MESSAGE_VIEW_TYPE_SENT_BY_ME || MESSAGE_SENT_TOP == viewType || viewType == MESSAGE_SENT_BOTTOM || viewType == MESSAGE_SENT_MIDDLE ){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_message, parent, false);
            return new messageViewHolder(view);
        }
        else if(viewType == MESSAGE_VIEW_TYPE_RECEIVED){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_message_received, parent, false);
            return new messageReceivedViewHolder(view);
        }
        return null;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof messageViewHolder){
            ((messageViewHolder) holder).messageTextView.setText(messageList.get(position).getMessage());
            if (getItemViewType(position) == MESSAGE_SENT_TOP){
                TextView textView = ((messageViewHolder) holder).messageTextView.findViewById(R.id.text_message);
                textView.setBackgroundResource(R.drawable.message_sent_top);
            }
            else if(getItemViewType(position) == MESSAGE_SENT_BOTTOM){
                TextView textView = ((messageViewHolder) holder).messageTextView;
                textView.setBackgroundResource(R.drawable.message_sent_bottom);
            }
            else if (getItemViewType(position) == MESSAGE_SENT_MIDDLE){
                TextView textView = ((messageViewHolder) holder).messageTextView.findViewById(R.id.text_message);
                textView.setBackgroundResource(R.drawable.message_sent_middle);
            }
        }
        else if (holder instanceof  messageReceivedViewHolder){
            ((messageReceivedViewHolder) holder).messageTextView.setText(messageList.get(position).getMessage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        boolean isThereAMessageAbove = (position - 1 >= 0);
        boolean isThereAMessageBelow = (position + 1 < messageList.size());

        if(messageList.get(position).wasSentByMe()){
            if(isThereAMessageAbove){
                //Message above was sent by me
                if(messageList.get(position - 1).wasSentByMe()){

                    if(isThereAMessageBelow){
                        //Message below was sent by me
                        if(messageList.get(position + 1).wasSentByMe()){
                            return MESSAGE_SENT_MIDDLE;
                        }
                        //Message below was NOT sent by me
                        else{
                            return MESSAGE_SENT_BOTTOM;
                        }
                    }
                    else{
                        return MESSAGE_SENT_BOTTOM;
                    }

                }
                //Message above was NOT sent by me
                else{
                    if(isThereAMessageBelow){
                        //Message below was also not sent by me
                        if(!messageList.get(position + 1).wasSentByMe()){
                            return MESSAGE_VIEW_TYPE_SENT_BY_ME;
                        }
                        //Message below was sent by me
                        else{
                            return  MESSAGE_SENT_TOP;
                        }
                    }
                    else{
                        return MESSAGE_VIEW_TYPE_SENT_BY_ME;
                    }
                }
            }
            //THERE IS NOT A MESSAGE ABOVE
            else{
                if(isThereAMessageBelow){
                    //Message below was sent by me
                    if(messageList.get(position + 1).wasSentByMe()){
                        return MESSAGE_SENT_TOP;
                    }
                    //Message below was not sent by me
                    else{
                        return  MESSAGE_VIEW_TYPE_SENT_BY_ME;
                    }
                }
                else{
                    return MESSAGE_VIEW_TYPE_SENT_BY_ME;
                }
            }
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

    public void addMessage(ConversationMessage message){
        messageList.add(message);
        notifyItemRangeInserted(messageList.size() - 1, 1);
    }
}
