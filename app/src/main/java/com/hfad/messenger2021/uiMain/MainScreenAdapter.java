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

import com.hfad.messenger2021.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.subjects.PublishSubject;

public class MainScreenAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<String> usernameList = new ArrayList<>();
    List<Bitmap> pictureList = new ArrayList<>();
    List<String> lastMessageList = new ArrayList<>();
    List<String> lastMessageTimestampList = new ArrayList<>();
    private final int EMPTY_VIEW_TYPE = 0;

    private final PublishSubject<String> onClickSubject = PublishSubject.create();


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == EMPTY_VIEW_TYPE){
            Log.d("MainScreenAdapter", "EMPTY_VIEW_TYPE");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_conversation_item, parent, false);
            return new emptyViewHolder(view);
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
            ((MainViewHolder) holder).usernameView.setText(usernameList.get(position));
            ((MainViewHolder) holder).lastMessageView.setText(lastMessageList.get(position));
            ((MainViewHolder) holder).lastMessageTimestamp.setText(lastMessageTimestampList.get(position));
            if(pictureList.size() > position){
                ((MainViewHolder) holder).imageView.setImageBitmap(pictureList.get(position));
            }
            else{
                ((MainViewHolder) holder).imageView.setImageResource(R.drawable.ic_baseline_account_circle_24);
            }
        }
        else{
            ((emptyViewHolder) holder).button.setOnClickListener(view -> {
                onClickSubject.onNext("click");
            });
        }
    }

    @Override
    public int getItemCount() {
        if (usernameList.size() == 0){
            return 1;
        }
        else{
            return usernameList.size();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (usernameList.size() == 0){
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

    public void setUsernameList(List<String> usernameList) {
        this.usernameList = usernameList;
    }

    public void setLastMessageList(List<String> lastMessageList){this.lastMessageList = lastMessageList;}

    public void setLastMessageTimestampList(List<String> lastMessageTimestampList) {this.lastMessageTimestampList = lastMessageTimestampList;
    }

    public PublishSubject<String> getClick(){
        return onClickSubject;
    }

}
