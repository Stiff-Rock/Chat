package com.stiffrock.chat.model;

import java.util.ArrayList;
import java.util.List;

public class CurrentUser {
    private static User currentUser;
    private static List<PrivateChat> contacts = new ArrayList<>();
    private static BaseChat currentGroupChat;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        CurrentUser.currentUser = currentUser;
    }

    public static List<PrivateChat> getContacts() {
        return contacts;
    }

    public static void setContacts(List<PrivateChat> contacts) {
        CurrentUser.contacts = contacts;
    }

    public static BaseChat getCurrentGroupChat() {
        return currentGroupChat;
    }

    public static void setCurrentGroupChat(BaseChat currentGroupChat) {
        CurrentUser.currentGroupChat = currentGroupChat;
    }

    public static BaseChat getCurrentChat() {
        return currentGroupChat;
    }

    public static void setCurrentChat(BaseChat currentGroupChat) {
        CurrentUser.currentGroupChat = currentGroupChat;
    }
}
