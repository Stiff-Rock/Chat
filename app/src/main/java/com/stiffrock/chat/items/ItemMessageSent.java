package com.stiffrock.chat.items;

import com.stiffrock.chat.model.MessageState;

public class ItemMessageSent extends Item {
    private final String sender;
    private String message;
    private final String timestamp;
    private MessageState messageState;

    public ItemMessageSent(String sender, String message, String timestamp, MessageState messageState) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.messageState = messageState;
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

    public MessageState getMessageState() {
        return messageState;
    }

    public void setMessageState(MessageState messageState) {
        this.messageState = messageState;
    }

    @Override
    public int getType() {
        return 1;
    }
}
