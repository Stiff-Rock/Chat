package com.stiffrock.chat.model;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.Set;

/**
 * Clase que representa a un usuario
 * </p>
 * Atributos:
 * <p>
 * - {@link #id}: Id único del usuario en la BBDD
 * <p>
 * - {@link #profilePictureUrl}: Url de la foto de perfil del usuario, almacenada en la BBDD
 * <p>
 * - {@link #username}: Nombre de usuario entre 3 y 20 caracteres
 * <p>
 * - {@link #chats}: Chats en los que participa este usuario
 */
public class User {
    private Long id;
    private String profilePictureUrl;
    private String username;
    private Set<BaseChat> chats;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<BaseChat> getChats() {
        return chats;
    }

    public void setChats(Set<BaseChat> chats) {
        this.chats = chats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" + "id=" + id + ", profilePictureUrl='" + profilePictureUrl + '\'' + ", username='" + username + '\'' + '}';
    }
}
