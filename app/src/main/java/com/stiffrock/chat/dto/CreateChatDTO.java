package com.stiffrock.chat.dto;

import com.stiffrock.chat.model.User;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatDTO {
    private String chatName;
    private boolean isGroupChat;
    private Set<User> participants;
}
