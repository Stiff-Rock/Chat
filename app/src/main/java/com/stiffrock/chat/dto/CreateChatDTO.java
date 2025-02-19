package com.stiffrock.chat.dto;

import java.util.Set;

public class CreateChatDTO {
    private String chatName;
    private boolean isGroupChat;
    private Set<Long> participants;

    public CreateChatDTO() {
    }

    public CreateChatDTO(String chatName, boolean isGroupChat, Set<Long> participants) {
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
                ", isGroupChat=" + isGroupChat +
                ", participants=" + participants +
                '}';
    }
}
