package com.stiffrock.chat.items;

import com.stiffrock.chat.model.Chat;

public class ItemContactCard extends Item {
    private String name;

    public ItemContactCard(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getType() {
        return 3;
    }
}
