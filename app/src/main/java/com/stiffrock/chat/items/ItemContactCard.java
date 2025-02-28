package com.stiffrock.chat.items;

import com.stiffrock.chat.model.User;

public class ItemContactCard extends Item {
    private byte[] photo;
    private String name;
    private User user;
    private boolean showOnlineStatus;
    private boolean isSelected;

    public ItemContactCard(User user, boolean showOnlineStatus, boolean isSelected) {
        this.photo = user.getProfilePicture();
        this.name = user.getUsername();
        this.user = user;
        this.showOnlineStatus = showOnlineStatus;
        this.isSelected = isSelected;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
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
