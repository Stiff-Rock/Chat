package com.stiffrock.chat.dto;

import androidx.annotation.NonNull;

import java.util.Set;

public class GroupChatDTO {
    private String chatName;
    private Set<Long> participants;
    private Long adminId;
    private byte[] chatPhoto;

    public GroupChatDTO() {
    }

    public GroupChatDTO(String chatName, Set<Long> participants, Long adminId, byte[] chatPhoto) {
        this.chatName = chatName;
        this.participants = participants;
        this.adminId = adminId;
        this.chatPhoto = chatPhoto;
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

    public byte[] getChatPhoto() {
        return chatPhoto;
    }

    public void setChatPhoto(byte[] chatPhoto) {
        this.chatPhoto = chatPhoto;
    }

    @NonNull
    @Override
    public String toString() {
        return "GroupChatDTO{" +
                "chatName='" + chatName + '\'' +
                ", participants=" + participants +
                ", adminId=" + adminId +
                '}';
    }
}
