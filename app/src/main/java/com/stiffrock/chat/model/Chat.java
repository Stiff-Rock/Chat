package com.stiffrock.chat.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    private long chatId;
    private String name;
    private boolean isGroupChat;
    private Set<User> participants;

    public Chat(String name, boolean isGroupChat, Set<User> participants) {
        this.name = name;
        this.isGroupChat = isGroupChat;
        this.participants = participants;
    }
}
