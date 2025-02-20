package com.stiffrock.chat.dto;

import java.util.Set;

public class GroupChatDTO {
    private String chatName;
    private Set<Long> participants;

    public GroupChatDTO() {
    }

    public GroupChatDTO(String chatName, Set<Long> participants) {
        this.chatName = chatName;
        this.participants = participants;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public Set<Long> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Long> participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        return "CreateChatDTO{" +
                "chatName='" + chatName + '\'' +
                ", participants=" + participants +
                '}';
    }
}
