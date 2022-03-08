package com.hfad.messenger2021.uiMain;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.messenger2021.Objects.ConversationObject;
import com.hfad.messenger2021.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.subjects.PublishSubject;

public class MainScreenAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<ConversationObject> conversationList = new ArrayList<>();
    private final int EMPTY_VIEW_TYPE = 0;
    private final int LOADING_VIEW_TYPE = 2;
    private final int NO_CONNECTION_VIEW_TYPE = 3;

    private Boolean isConnectionWorking = null;

    private final PublishSubject<String> onClickSubject = PublishSubject.create();
    private final PublishSubject<ConversationObject> onFriendClickSubject = PublishSubject.create();


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == EMPTY_VIEW_TYPE){
            Log.d("MainScreenAdapter", "EMPTY_VIEW_TYPE");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_item_empty, parent, false);
            return new emptyViewHolder(view);
        }
        else if(viewType == LOADING_VIEW_TYPE){
            Log.d("MainScreenAdapter", "LOADING_VIEW_TYPE");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_item_loading, parent, false);
            return new loadingViewHolder(view);
        }
        else if(viewType == NO_CONNECTION_VIEW_TYPE){
            Log.d("MainScreenAdapter", "NO_CONNECTION_VIEW_TYPE");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_item_no_connection, parent, false);
            return new noConnectionViewHolder(view);
        }
        else{
            Log.d("MainScreenAdapter", "BASIC_VIEW_TYPE");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_item, parent, false);
            return new MainViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MainViewHolder){
            ((MainViewHolder) holder).usernameView.setText(conversationList.get(position).getConversationName());
            ((MainViewHolder) holder).lastMessageView.setText(conversationList.get(position).getConversationLastMessage());
            ((MainViewHolder) holder).lastMessageTimestamp.setText(conversationList.get(position).getGetConversationLastMessageTimeStamp());
            if(conversationList.get(position).getConversationProfilePic() != null){
                ((MainViewHolder) holder).imageView.setImageBitmap(conversationList.get(position).getConversationProfilePic());
            }
            else{
                ((MainViewHolder) holder).imageView.setImageResource(R.drawable.ic_baseline_account_circle_24);
            }

            ((MainViewHolder) holder).itemView.setOnClickListener(view -> {
                ConversationObject conversationObject = conversationList.get(position);
                onFriendClickSubject.onNext(conversationObject);
            });
        }
        else if (holder instanceof emptyViewHolder){
            ((emptyViewHolder) holder).button.setOnClickListener(view -> {
                onClickSubject.onNext("click");
            });
        }
    }

    @Override
    public int getItemCount() {
        if (conversationList.size() == 0){
            return 1;
        }
        else{
            return conversationList.size();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (isConnectionWorking == null){
            return LOADING_VIEW_TYPE;
        }
        if(!isConnectionWorking){
            return NO_CONNECTION_VIEW_TYPE;
        }
        if (conversationList.size() == 0){
            return EMPTY_VIEW_TYPE;
        }
        else{
            return 1;
        }
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder{
        public final TextView usernameView;
        public final ImageView imageView;
        public final TextView lastMessageView;
        public final TextView lastMessageTimestamp;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameView = itemView.findViewById(R.id.conversation_name);
            imageView = itemView.findViewById(R.id.conversation_image);
            lastMessageView = itemView.findViewById(R.id.conversation_last_message);
            lastMessageTimestamp = itemView.findViewById(R.id.conversation_date);
        }
    }

    public static class emptyViewHolder extends RecyclerView.ViewHolder{
        public final ImageView image;
        public final TextView text;
        public final Button button;

        public emptyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.empty_conversation_image);
            text = itemView.findViewById(R.id.empty_conversation_text);
            button = itemView.findViewById(R.id.empty_conversation_button);
        }
    }

    public static class loadingViewHolder extends RecyclerView.ViewHolder{
        public loadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class noConnectionViewHolder extends RecyclerView.ViewHolder{
        public noConnectionViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setConversationList(List<ConversationObject> conversationList) { this.conversationList = conversationList;}

    public PublishSubject<String> getClick(){
        return onClickSubject;
    }

    public PublishSubject<ConversationObject> getOnFriendClick() {return onFriendClickSubject;}

    public void setIsConnectionWorking(Boolean isConnectionWorking){
        this.isConnectionWorking = isConnectionWorking;
    }

}
