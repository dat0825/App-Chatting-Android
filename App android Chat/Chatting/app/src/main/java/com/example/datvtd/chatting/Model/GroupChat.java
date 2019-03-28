package com.example.datvtd.chatting.Model;

import java.util.List;

public class GroupChat {
    public String idGroup;
    public String admin;
    public String imageGroupURL;
    public String nameGroup;
    public String color;

    public GroupChat() {

    }

    public GroupChat(String idGroup, String admin, String imageGroupURL, String nameGroup) {
        this.idGroup = idGroup;
        this.admin = admin;
        this.imageGroupURL = imageGroupURL;
        this.nameGroup = nameGroup;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getImageGroupURL() {
        return imageGroupURL;
    }

    public void setImageGroupURL(String imageGroupURL) {
        this.imageGroupURL = imageGroupURL;
    }

    public String getNameGroup() {
        return nameGroup;
    }

    public void setNameGroup(String nameGroup) {
        this.nameGroup = nameGroup;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
