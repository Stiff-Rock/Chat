package com.stiffrock.chat.model;

import lombok.Getter;
import lombok.Setter;

public class CurrentUser {
    @Getter
    @Setter
    private static User currentUser;
    @Getter
    @Setter
    private static Chat currentChat;
}
