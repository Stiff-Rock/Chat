package com.stiffrock.chat.model;

import java.util.Set;

public abstract class BaseChat {
    private long chatId;

    private Set<User> participants;

    public BaseChat() {
    }

    // Getters y setters comunes
    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }
}
