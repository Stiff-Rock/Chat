package com.stiffrock.chat.model;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.util.Log;

public class CurrentUser {
    private static User currentUser;
    private static BaseChat currentGroupChat;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        CurrentUser.currentUser = currentUser;
    }

    public static BaseChat getCurrentChat() {
        return currentGroupChat;
    }

    public static void setCurrentChat(BaseChat currentGroupChat) {
        CurrentUser.currentGroupChat = currentGroupChat;
    }
}
