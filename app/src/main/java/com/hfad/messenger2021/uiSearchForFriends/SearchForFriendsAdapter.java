package com.hfad.messenger2021.uiSearchForFriends;

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

public class SearchForFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> nameList = new ArrayList<>();
    private List<Integer> idList = new ArrayList<>();

    private final int EMPTY_VIEW_TYPE = 0;

    private final PublishSubject<Integer> clickPublishSubject = PublishSubject.create();


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == EMPTY_VIEW_TYPE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_for_friends_empty, parent, false);
            return new emptyViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_for_friends_item, parent, false);
            return new searchForFriendsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof searchForFriendsViewHolder) {
            ((searchForFriendsViewHolder) holder).name.setText(nameList.get(position));
            ((searchForFriendsViewHolder) holder).button.setOnClickListener(view -> clickPublishSubject.onNext(idList.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        if (idList.size() == 0){
            return 1;
        }
        return idList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(idList.size() == 0){
            return EMPTY_VIEW_TYPE;
        }
        return super.getItemViewType(position);
    }

    public static class searchForFriendsViewHolder extends RecyclerView.ViewHolder{
        public final ImageView image;
        public final TextView name;
        public final ImageView button;

        public searchForFriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.add_friend_image);
            name = itemView.findViewById(R.id.add_friend_name);
            button = itemView.findViewById(R.id.add_friend_button);
        }
    }

    public static class emptyViewHolder extends RecyclerView.ViewHolder{
        public emptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setIdList(List<Integer> idList){this.idList = idList;}

    public void setNameList(List<String> nameList){this.nameList = nameList;}

    public PublishSubject<Integer> getClickPublishSubject() {
        return clickPublishSubject;
    }
}
