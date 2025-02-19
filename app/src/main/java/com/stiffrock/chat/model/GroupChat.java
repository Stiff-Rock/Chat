package com.stiffrock.chat.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Set;

@Entity
@Table(name = "group_chats")
public class GroupChat extends BaseChat {
    private String name;

    public GroupChat() {
    }

    public GroupChat(String name, Set<User> participants) {
        setParticipants(participants);
        this.name = name;
    }

    // Getters y setters específicos de los chats grupales
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "GroupChat{" + "chatId=" + getChatId() + ", name='" + name + '\'' + '}';
    }
}