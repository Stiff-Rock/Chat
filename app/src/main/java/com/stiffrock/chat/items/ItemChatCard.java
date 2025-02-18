package com.stiffrock.chat.items;

import com.stiffrock.chat.R;
import com.stiffrock.chat.model.Chat;

public class ItemChatCard extends Item {
    private String chatName;
    private boolean isGroupChat;

    public ItemChatCard(String chatName, boolean isGroupChat) {
        this.chatName = chatName;
        this.isGroupChat = isGroupChat;
    }

    public ItemChatCard(Chat chat) {
        this.chatName = chat.getName();
        this.isGroupChat = chat.isGroupChat();
    }

    // Getters
    public String getChatName() {
        return chatName;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    //Setters
    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public void setGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }

    @Override
    public int getType() {
        return 2;
    }
}
