package com.stiffrock.chat.items;

public class ItemMessageSent extends Item{
    private String message;

    public ItemMessageSent(String textView) {
        this.message = textView;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int getType() {
        return 1;
    }
}
