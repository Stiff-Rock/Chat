package com.stiffrock.chat.items;

import com.stiffrock.chat.model.Chat;

public class ItemChatCard extends Item {
    private String chatName;
    //TODO: DELTE THIS HANDLE DIFFERENTLY
    private Chat chat;

    public ItemChatCard(Chat chat) {
        this.chatName = chat.getName();
        this.chat = chat;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    @Override
    public int getType() {
        return 2;
    }
}
