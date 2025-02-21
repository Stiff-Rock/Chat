package com.stiffrock.chat.dto;

import java.util.Set;

public class GroupChatDTO {
    private String chatName;
    private Set<Long> participants;
    private Long adminId;

    public GroupChatDTO() {
    }

    public GroupChatDTO(String chatName, Set<Long> participants, Long adminId) {
        this.chatName = chatName;
        this.participants = participants;
        this.adminId = adminId;
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

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    @Override
    public String toString() {
        return "GroupChatDTO{" +
                "chatName='" + chatName + '\'' +
                ", participants=" + participants +
                ", adminId=" + adminId +
                '}';
    }
}
