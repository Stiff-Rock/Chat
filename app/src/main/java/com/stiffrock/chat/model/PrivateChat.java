package com.stiffrock.chat.model;

import jakarta.persistence.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "private_chats")
public class PrivateChat extends BaseChat {
    @Transient
    private String name;

    @Column(unique = true)
    private String uniqueHash;

    public PrivateChat() {
    }

    public PrivateChat(User user1, User user2) {
        Set<User> participants = new HashSet<>();
        participants.add(user1);
        participants.add(user2);
        setParticipants(participants);
        generateUniqueHash();
    }

    @PostLoad
    private void generateNames() {
        List<User> sortedUsers = getParticipants().stream().sorted(Comparator.comparing(User::getId)).toList();
        this.name = sortedUsers.get(0).getUsername() + " & " + sortedUsers.get(1).getUsername();
    }

    private void generateUniqueHash() {
        List<Long> ids = getParticipants().stream().map(User::getId).sorted().toList();
        this.uniqueHash = ids.get(0) + ":" + ids.get(1);
    }

    // Getters y setters específicos de los chats privados
    public String getUniqueHash() {
        return uniqueHash;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "PrivateChat{" + "chatId=" + getChatId() + ", name='" + name + '\'' + '}';
    }
}
