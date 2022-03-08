package com.hfad.messenger2021.Objects;

public class ConversationMessage {
    private String message;
    private boolean wasSentByMe;
    private String messageDate;

    public ConversationMessage(String message, boolean wasSentByMe) {
        this.message = message;
        this.wasSentByMe = wasSentByMe;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean wasSentByMe() {
        return wasSentByMe;
    }

    public void setWasSentByMe(boolean wasSentByMe) {
        this.wasSentByMe = wasSentByMe;
    }
}
