package com.example.datvtd.chatting.Notifications;

public class Data {
    String user, body, title, sented;
    int icon;

    public Data() {
    }

    public Data(String user, int icon, String body, String title, String sented) {
        this.user = user;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.icon = icon;
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

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
