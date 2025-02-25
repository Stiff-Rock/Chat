package com.stiffrock.chat.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message {
    private Long id;
    private User sender;
    private BaseChat chat;
    private String messageContent;
    private LocalDateTime timestamp;
    private boolean deleted = false;

    public Message() {
    }

    public Message(User sender, BaseChat chat, String messageContent, LocalDateTime timestamp) {
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

    public BaseChat getChat() {
        return chat;
    }

    public void setChat(BaseChat groupChat) {
        this.chat = groupChat;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Message{" + "id=" + id + ", sender=" + sender + ", chat=" + chat + ", messageContent='" + messageContent + '\'' + ", timestamp=" + timestamp + '}';
    }
}
