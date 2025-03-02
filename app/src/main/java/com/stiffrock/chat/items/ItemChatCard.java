package com.stiffrock.chat.items;

import com.stiffrock.chat.model.BaseChat;
import com.stiffrock.chat.utils.CurrentUser;
import com.stiffrock.chat.model.GroupChat;
import com.stiffrock.chat.model.PrivateChat;
import com.stiffrock.chat.model.User;

/**
 * Elemento de RecyclerView que representa un chat
 * <p>
 * Extiende de {@link Item}
 * </p>
 * Atributos:
 * <p>
 * - {@link #photoUrl}: Url de la foto almacenada en el servidor, asociada con este item.
 * <p>
 * - {@link #chatName}: Nombre del chat
 * <p>
 * - {@link #chat}: Referencia al objeto {@link BaseChat} al que representa
 */
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
