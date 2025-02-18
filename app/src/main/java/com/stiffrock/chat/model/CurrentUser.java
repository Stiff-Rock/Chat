package com.stiffrock.chat.model;

import com.stiffrock.chat.dto.UserDTO;

public class CurrentUser {
    private static UserDTO currentUser;

    public static UserDTO getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(UserDTO newUser) {
        currentUser = newUser;
    }
}
