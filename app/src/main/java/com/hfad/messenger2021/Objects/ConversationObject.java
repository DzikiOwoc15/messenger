package com.hfad.messenger2021.Objects;

import android.graphics.Bitmap;

//Object used to pass data from main screen to the specific conversation
public class ConversationObject {
    private String friendsName;
    private Bitmap friendsProfilePic;
    private int friendsId;


    public ConversationObject(String friendsName, int friendsId) {
        this.friendsName = friendsName;
        this.friendsId = friendsId;
    }

    public String getFriendsName() {
        return friendsName;
    }

    public void setFriendsName(String friendsName) {
        this.friendsName = friendsName;
    }

    public Bitmap getFriendsProfilePic() {
        return friendsProfilePic;
    }

    public void setFriendsProfilePic(Bitmap friendsProfilePic) {
        this.friendsProfilePic = friendsProfilePic;
    }

    public int getFriendsId() {
        return friendsId;
    }

    public void setFriendsId(int friendsId) {
        this.friendsId = friendsId;
    }
}
