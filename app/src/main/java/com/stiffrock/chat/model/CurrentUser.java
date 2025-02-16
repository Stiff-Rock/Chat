package com.stiffrock.chat.model;

public class CurrentUser {
    private static String username = null;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String newUsername) {
        username = newUsername;
    }
}
