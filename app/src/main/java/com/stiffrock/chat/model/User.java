package com.stiffrock.chat.model;

public class User {
    private static String username = null;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String newUsername) {
        username = newUsername;
    }
}
