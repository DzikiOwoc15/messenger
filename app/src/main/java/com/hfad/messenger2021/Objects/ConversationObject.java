package com.hfad.messenger2021.Objects;

import android.graphics.Bitmap;

//Object used to pass data from MainScreenAdapter via  mainScreenFragment to the specific conversationFragment
public class ConversationObject {
    private int conversationId;
    private String conversationName;
    private Bitmap conversationProfilePic;
    private String conversationLastMessage;
    private String getConversationLastMessageTimeStamp;

    public ConversationObject(String conversationName, int conversationId) {
        this.conversationName = conversationName;
        this.conversationId = conversationId;
    }

    public String getConversationLastMessage() {
        return conversationLastMessage;
    }

    public void setConversationLastMessage(String conversationLastMessage) {
        this.conversationLastMessage = conversationLastMessage;
    }

    public String getGetConversationLastMessageTimeStamp() {
        return getConversationLastMessageTimeStamp;
    }

    public void setGetConversationLastMessageTimeStamp(String getConversationLastMessageTimeStamp) {
        this.getConversationLastMessageTimeStamp = getConversationLastMessageTimeStamp;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public Bitmap getConversationProfilePic() {
        return conversationProfilePic;
    }

    public void setConversationProfilePic(Bitmap conversationProfilePic) {
        this.conversationProfilePic = conversationProfilePic;
    }
}
