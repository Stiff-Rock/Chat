package com.stiffrock.chat.dto;

import com.stiffrock.chat.model.Chat;
import com.stiffrock.chat.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private User sender;
    private Chat chat;
    private String messageContent;
}
