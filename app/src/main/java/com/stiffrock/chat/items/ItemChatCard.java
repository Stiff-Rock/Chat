package com.stiffrock.chat.items;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.util.Log;

import com.stiffrock.chat.model.BaseChat;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.GroupChat;
import com.stiffrock.chat.model.PrivateChat;
import com.stiffrock.chat.model.User;

public class ItemChatCard extends Item {
    private String photoUrl;
    private String chatName;
    private BaseChat chat;
    private boolean isOnline;

    public ItemChatCard(BaseChat chat) {
        if (chat instanceof PrivateChat) {
            User contact = ((PrivateChat) chat).getContact(CurrentUser.getCurrentUser());

            this.photoUrl = contact.getProfilePictureUrl();
            this.chatName = contact.getUsername();
        } else if (chat instanceof GroupChat) {
            this.photoUrl = ((GroupChat) chat).getChatPhotoUrl();
            this.chatName = ((GroupChat) chat).getName();
        }

        this.chat = chat;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    @Override
    public int getType() {
        return 2;
    }
}
