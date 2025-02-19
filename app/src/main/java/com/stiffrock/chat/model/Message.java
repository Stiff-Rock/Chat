package com.stiffrock.chat.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Long id;
    private User sender;
    private Chat chat;
    private Set<User> recipients = new HashSet<>();
    private String messageContent;
    private LocalDateTime timestamp;

    public Message(User sender, Chat chat, Set<User> recipients, String messageContent, LocalDateTime timestamp) {
        this.sender = sender;
        this.chat = chat;
        this.recipients = recipients;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
    }
}
