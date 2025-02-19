package com.stiffrock.chat.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


public class Message {
    private Long id;
    private User sender;
    private Chat chat;
    private Set<User> recipients = new HashSet<>();
    private String messageContent;
    private LocalDateTime timestamp;

    public Message() {
    }

    public Message(User sender, Chat chat, Set<User> recipients, String messageContent, LocalDateTime timestamp) {
        this.sender = sender;
        this.chat = chat;
        this.recipients = recipients;
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

    public Set<User> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<User> recipients) {
        this.recipients = recipients;
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
