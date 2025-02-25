package com.stiffrock.chat.items;

public class ItemMessageSent extends Item{
    private final String sender;
    private String message;
    private final String timestamp;

    public ItemMessageSent(String sender, String message, String timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int getType() {
        return 1;
    }
}
