package com.stiffrock.chat.items;

import com.stiffrock.chat.model.User;

public class ItemContactCard extends Item {
    private String name;
    private User user;

    public ItemContactCard(String name) {
        this.name = name;
    }

    public ItemContactCard(User user) {
        this.name = user.getUsername();
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int getType() {
        return 3;
    }
}
