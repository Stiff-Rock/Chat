package com.stiffrock.chat.items;

import com.stiffrock.chat.model.BaseChat;
import com.stiffrock.chat.model.GroupChat;
import com.stiffrock.chat.model.PrivateChat;

public class ItemChatCard extends Item {
    private String chatName;
    private BaseChat chat;

    public ItemChatCard(BaseChat chat) {
        if (chat instanceof PrivateChat) {
            this.chatName = ((PrivateChat) chat).getName();
        } else if (chat instanceof GroupChat) {
            this.chatName = ((GroupChat) chat).getName();
        }

        this.chat = chat;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public BaseChat getChat() {
        return chat;
    }

    public void setChat(BaseChat groupChat) {
        this.chat = groupChat;
    }

    @Override
    public int getType() {
        return 2;
    }
}
