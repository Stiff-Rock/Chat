package com.stiffrock.chat.model;

import java.time.LocalDateTime;

public class Message {
    private Long id;
    private User sender;
    private Chat chat;
    private String messageContent;
    private LocalDateTime timestamp;

    public Message() {
    }

    public Message(User sender, Chat chat, String messageContent, LocalDateTime timestamp) {
        this.sender = sender;
        this.chat = chat;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", sender=" + sender +
                ", chat=" + chat +
                ", messageContent='" + messageContent + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
