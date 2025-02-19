package com.stiffrock.chat.model;

public class CurrentUser {
    private static User currentUser;
    private static Chat currentChat;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        CurrentUser.currentUser = currentUser;
    }

    public static Chat getCurrentChat() {
        return currentChat;
    }

    public static void setCurrentChat(Chat currentChat) {
        CurrentUser.currentChat = currentChat;
    }


}
