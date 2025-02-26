package com.stiffrock.chat.model;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Message {
    private Long id;
    private User sender;
    private BaseChat chat;
    private String messageContent;
    private LocalDateTime timestamp;
    private MessageState messageState;

    private boolean deleted = false;

    public Message() {
    }

    public Message(User sender, BaseChat chat, String messageContent, LocalDateTime timestamp) {
        this.sender = sender;
        this.chat = chat;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
        messageState = MessageState.SENT;
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

    public MessageState getMessageState() {
        return messageState;
    }

    public void setMessageState(MessageState messageState) {
        this.messageState = messageState;
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

    @NonNull
    @Override
    public String toString() {
        return "Message{" + "id=" + id + ", sender=" + sender + ", chat=" + chat + ", messageContent='" + messageContent + '\'' + ", timestamp=" + timestamp + ", messageState=" + messageState + ", deleted=" + deleted + '}';
    }
}
