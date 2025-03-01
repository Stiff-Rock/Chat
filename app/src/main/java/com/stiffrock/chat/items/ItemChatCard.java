package com.stiffrock.chat.items;

import com.stiffrock.chat.model.BaseChat;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.GroupChat;
import com.stiffrock.chat.model.PrivateChat;

public class ItemChatCard extends Item {
    private String photoUrl;
    private String chatName;
    private BaseChat chat;
    private boolean isOnline;

    public ItemChatCard(BaseChat chat) {
        if (chat instanceof PrivateChat) {
            photoUrl = ((PrivateChat) chat).getContact(CurrentUser.getCurrentUser()).getProfilePictureUrl();
            String name = ((PrivateChat) chat).getName();
            String[] usernames = name.split("&");
            String currentUsrName = CurrentUser.getCurrentUser().getUsername();
            this.chatName = usernames[0].equals(currentUsrName) ? usernames[1] : usernames[0];
        } else if (chat instanceof GroupChat) {
//       TODO     photoUrl = ((GroupChat) chat).getChatPhoto();
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
