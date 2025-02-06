package com.stiffrock.chat.items;

public class ItemMessageRecieved extends Item{
    private String message;

    public ItemMessageRecieved(String textView) {
        this.message = textView;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int getType() {
        return 0;
    }
}
