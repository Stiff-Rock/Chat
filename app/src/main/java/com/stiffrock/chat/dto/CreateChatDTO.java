package com.stiffrock.chat.dto;

import java.util.Set;

public class CreateChatDTO {
    private String chatName;
    private boolean groupChat;
    private Set<Long> participants;

    public CreateChatDTO() {
    }

    public CreateChatDTO(String chatName, boolean groupChat, Set<Long> participants) {
        this.chatName = chatName;
        this.groupChat = groupChat;
        this.participants = participants;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public boolean getGroupChat() {
        return groupChat;
    }

    public void setGroupChat(boolean groupChat) {
        this.groupChat = groupChat;
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
                ", isGroupChat=" + groupChat +
                ", participants=" + participants +
                '}';
    }
}
