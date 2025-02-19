package com.stiffrock.chat.items;

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
