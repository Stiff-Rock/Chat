package com.stiffrock.chat.dto;

import com.stiffrock.chat.model.User;

import java.util.Set;

public class CreateChatDTO {
    private String chatName;
    private boolean isGroupChat;
    private Set<User> participants;

    public CreateChatDTO() {
    }

    public CreateChatDTO(String chatName, boolean isGroupChat, Set<User> participants) {
        this.chatName = chatName;
        this.isGroupChat = isGroupChat;
        this.participants = participants;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public void setGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }
}
