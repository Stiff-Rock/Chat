package com.stiffrock.chat.items;

import com.stiffrock.chat.model.User;

/**
 * Elemento de RecyclerView que representa a un contacto
 * <p>
 * Extiende de {@link Item}
 * </p>
 * Atributos:
 * <p>
 * - {@link #photoUrl}: Url de la foto de perfil del usuario almacenada en el servidor, asociada con este item.
 * <p>
 * - {@link #name}: Nombre del usuario
 * <p>
 * - {@link #user}: Referencia al objeto {@link User} al que representa
 * <p>
 * - {@link #showOnlineStatus}: Boolean que representa si el usuario está actualmente conectado o no
 * <p>
 * - {@link #isSelected}: Booolean que controla si el item esta seleccionado o no para cambiar su renderización
 */
public class ItemContactCard extends Item {
    private String photoUrl;
    private String name;
    private User user;
    private boolean showOnlineStatus;
    private boolean isSelected;

    public ItemContactCard(User user, boolean showOnlineStatus, boolean isSelected) {
        this.photoUrl = user.getProfilePictureUrl();
        this.name = user.getUsername();
        this.user = user;
        this.showOnlineStatus = showOnlineStatus;
        this.isSelected = isSelected;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean showOnlineStatus() {
        return showOnlineStatus;
    }

    public void setShowOnlineStatus(boolean showOnlineStatus) {
        this.showOnlineStatus = showOnlineStatus;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int getType() {
        return 3;
    }
}
