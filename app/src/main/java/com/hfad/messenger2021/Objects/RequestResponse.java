package com.hfad.messenger2021.Objects;

public class RequestResponse {
    private boolean isAccepted;
    private int friendId;

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        this.isAccepted = accepted;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public RequestResponse(boolean isAccepted, int friendId) {
        this.isAccepted = isAccepted;
        this.friendId = friendId;
    }
}
