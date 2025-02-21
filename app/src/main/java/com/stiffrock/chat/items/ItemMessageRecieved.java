package com.stiffrock.chat.items;

public class ItemMessageRecieved extends Item{
    private final String sender;
    private final String message;
    private final String timestamp;

    public ItemMessageRecieved(String sender, String message, String timestamp) {
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

    @Override
    public int getType() {
        return 0;
    }
}
