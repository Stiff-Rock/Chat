package com.stiffrock.chat.items;

import com.stiffrock.chat.model.GroupChat;

public class ItemChatCard extends Item {
    private String chatName;
    //TODO: DELTE THIS HANDLE DIFFERENTLY
    private GroupChat groupChat;

    public ItemChatCard(GroupChat groupChat) {
        this.chatName = groupChat.getName();
        this.groupChat = groupChat;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public GroupChat getChat() {
        return groupChat;
    }

    public void setChat(GroupChat groupChat) {
        this.groupChat = groupChat;
    }

    @Override
    public int getType() {
        return 2;
    }
}
