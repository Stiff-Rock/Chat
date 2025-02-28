package com.stiffrock.chat.model;

import androidx.annotation.NonNull;

import java.util.Set;

public class GroupChat extends BaseChat {
    private String name;
    private Set<User> admins;
    private byte[] chatPhoto;

    public GroupChat() {
    }

    public GroupChat(String name, Set<User> participants, Set<User> admins) {
        setParticipants(participants);
        this.name = name;
        this.admins = admins;
    }

    // Getters y setters específicos de los chats grupales
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<User> admins) {
        this.admins = admins;
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
        return "GroupChat{" + "id='" + getId() + '\'' + ", name=' " + name + '\'' + ", admins = " + admins + '}';
    }
}