package com.stiffrock.chat.dto;

import java.util.List;

public class CreateGroupRequest {
    private String groupName;
    private List<UserDTO> participants;

    public CreateGroupRequest(String groupName, List<UserDTO> participants) {
        this.groupName = groupName;
        this.participants = participants;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<UserDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserDTO> participants) {
        this.participants = participants;
    }
}
