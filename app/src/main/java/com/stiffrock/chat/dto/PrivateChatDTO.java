package com.stiffrock.chat.dto;

public class PrivateChatDTO {
    private Long userId1;
    private Long userId2;

    public PrivateChatDTO() {
    }

    public PrivateChatDTO(Long userId1, Long userId2) {
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    public Long getUserId1() {
        return userId1;
    }

    public void setUserId1(Long userId1) {
        this.userId1 = userId1;
    }

    public Long getUserId2() {
        return userId2;
    }

    public void setUserId2(Long userId2) {
        this.userId2 = userId2;
    }

    @Override
    public String toString() {
        return "PrivateChatDTO{" +
                "userId1=" + userId1 +
                ", userId2=" + userId2 +
                '}';
    }
}
