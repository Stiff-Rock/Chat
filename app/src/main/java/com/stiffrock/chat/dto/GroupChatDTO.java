package com.stiffrock.chat.dto;

import androidx.annotation.NonNull;

import java.util.Set;

/**
 * Data Tansfer Object para estandarizar la serialización de las solicitudes de
 * creación de chat grupales.
 */
public class GroupChatDTO {
    private String chatName;
    private Set<Long> participants;
    private Long adminId;
    private String chatPhotoUrl;

    public GroupChatDTO() {
    }

    public GroupChatDTO(String chatName, Set<Long> participants, Long adminId, String chatPhotoUrl) {
        this.chatName = chatName;
        this.participants = participants;
        this.adminId = adminId;
        this.chatPhotoUrl = chatPhotoUrl;
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

    public String getChatPhotoUrl() {
        return chatPhotoUrl;
    }

    public void setChatPhotoUrl(String chatPhotoUrl) {
        this.chatPhotoUrl = chatPhotoUrl;
    }


    @NonNull
    @Override
    public String toString() {
        return "GroupChatDTO{" + "chatName='" + chatName + '\'' + ", participants=" + participants + ", adminId=" + adminId + ", chatPhotoUrl='" + chatPhotoUrl + '\'' + '}';
    }
}
