package com.stiffrock.chat.model;

import java.util.Set;

public class Chat {
    private long chatId;
    private String name;
    private boolean isGroupChat;
    private Set<User> participants;

    public Chat() {
    }

    public Chat(String name, boolean isGroupChat, Set<User> participants) {
        this.name = name;
        this.isGroupChat = isGroupChat;
        this.participants = participants;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Chat{" +
                "chatId=" + chatId +
                ", name='" + name + '\'' +
                ", isGroupChat=" + isGroupChat +
                ", participants=" + participants +
                '}';
    }
}
