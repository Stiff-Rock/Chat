package com.stiffrock.chat.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Message {
    private Long id;
    private Long senderId;
    private Chat chat;
    private Set<Long> recipientsId = new HashSet<>();
    private String messageContent;
    private LocalDateTime timestamp;

    public Message() {
    }

    public Message(Long senderId, Chat chat, Set<Long> recipientsId, String messageContent, LocalDateTime timestamp) {
        this.senderId = senderId;
        this.chat = chat;
        this.recipientsId = recipientsId;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Set<Long> getRecipientsId() {
        return recipientsId;
    }

    public void setRecipientsId(Set<Long> recipientsId) {
        this.recipientsId = recipientsId;
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
}
