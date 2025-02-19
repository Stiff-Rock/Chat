package com.stiffrock.chat.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private Set<Chat> chats;

    public User(String username) {
        this.username = username;
    }
}
