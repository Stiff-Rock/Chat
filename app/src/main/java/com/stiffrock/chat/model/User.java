package com.stiffrock.chat.model;

import java.util.Set;

public class User {
    private Long id;
    private String username;
    private Set<GroupChat> groupChats;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<GroupChat> getChats() {
        return groupChats;
    }

    public void setChats(Set<GroupChat> groupChats) {
        this.groupChats = groupChats;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
