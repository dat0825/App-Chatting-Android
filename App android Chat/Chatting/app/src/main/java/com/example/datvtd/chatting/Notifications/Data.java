package com.example.datvtd.chatting.Notifications;

public class Data {
    public String user;
    public String body;
    public String title;
    public String sented;
    public String typeNotification;

    public Data() {
    }

    public Data(String user, String typeNotification, String body, String title, String sented) {
        this.user = user;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.typeNotification = typeNotification;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }

    public String getIcon() {
        return typeNotification;
    }

    public void setIcon(String typeNotification) {
        this.typeNotification = typeNotification;
    }
}