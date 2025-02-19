package com.stiffrock.chat.model;

public class CurrentUser {
    private static User currentUser;
    private static GroupChat currentGroupChat;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        CurrentUser.currentUser = currentUser;
    }

    public static GroupChat getCurrentChat() {
        return currentGroupChat;
    }

    public static void setCurrentChat(GroupChat currentGroupChat) {
        CurrentUser.currentGroupChat = currentGroupChat;
    }


}
