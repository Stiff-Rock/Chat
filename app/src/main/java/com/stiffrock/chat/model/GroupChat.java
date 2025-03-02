package com.stiffrock.chat.model;

import androidx.annotation.NonNull;

import java.util.Set;

/**
 * Clase que representa un chat grupal
 * </p>
 * Hereda de {@link BaseChat}
 * </p>
 * Atributos:
 * <p>
 * - {@link #name}: Nombre del chat actual. No tiene porque ser único.
 * <p>
 * - {@link #admins}: Set de {@link User} que son administradores en este grupo (al menos uno)
 * <p>
 * - {@link #chatPhotoUrl}: Url de la foto del grupo almacenada en el servidor
 */
public class GroupChat extends BaseChat {
    private String name;
    private Set<User> admins;
    private String chatPhotoUrl;

    public GroupChat() {
    }

    public GroupChat(String name, Set<User> participants, Set<User> admins) {
        setParticipants(participants);
        this.name = name;
        this.admins = admins;
    }

    // Getters y setters específicos de los chats grupales
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<User> admins) {
        this.admins = admins;
    }

    public String getChatPhotoUrl() {
        return chatPhotoUrl;
    }

    public void setChatPhotoUrl(String chatPhotoUrl) {
        this.chatPhotoUrl = chatPhotoUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "GroupChat{" + "id='" + getId() + '\'' + ", name=' " + name + '\'' + ", admins = " + admins + '}';
    }
}