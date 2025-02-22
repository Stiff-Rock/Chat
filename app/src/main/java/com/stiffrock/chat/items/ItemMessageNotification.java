package com.stiffrock.chat.items;

public class ItemMessageNotification extends Item{
    private final String message;

    public ItemMessageNotification(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int getType() {
        return 4;
    }
}
