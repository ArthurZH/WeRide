package com.example.deanwen.myapplication;

/**
 * Created by liuchang on 8/1/15.
 */
public class FriendItem {
    private String name;
    private boolean selected = false;
    private String email;

    public FriendItem(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
