package com.stiffrock.chat.utils;

import com.stiffrock.chat.model.BaseChat;
import com.stiffrock.chat.model.PrivateChat;
import com.stiffrock.chat.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase estática de utilidad que almacena al información del usuario actualmetne usando la aplicación
 * </p>
 * Atributos:
 * <p>
 * - {@link #currentUser}: Referencia al objeto {@link User} del usuario logeado acutalmente
 * <p>
 * - {@link #contacts}: Lista de contactos que este usuario posee.
 * <p>
 * - {@link #currentGroupChat}: Chat en el que se encuentra actualmente el usuario (puede ser null).
 */
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
