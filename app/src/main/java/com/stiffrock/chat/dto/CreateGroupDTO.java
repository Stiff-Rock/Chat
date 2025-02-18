package com.stiffrock.chat.dto;

import com.stiffrock.chat.model.User;

import java.util.List;

public class CreateGroupDTO {
    private String groupName;
    private List<User> participants;

    public CreateGroupDTO(String groupName, List<User> participants) {
        this.groupName = groupName;
        this.participants = participants;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }
}
