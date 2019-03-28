package com.example.datvtd.chatting.Model;

public class Chat {

    public String sender;
    public String receiver;
    public String message;
    public boolean isseen;
    public boolean isGroup;
    public boolean typeImage;

    public Chat(String sender, String receiver, String message, boolean isseen, boolean isGroup, boolean typeImage) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.isGroup = isGroup;
        this.typeImage = typeImage;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public boolean isTypeImage() {
        return typeImage;
    }

    public void setTypeImage(boolean typeImage) {
        this.typeImage = typeImage;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean isGroup) {
        isGroup = isGroup;
    }
}
