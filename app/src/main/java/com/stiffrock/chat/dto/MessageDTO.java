package com.stiffrock.chat.dto;

public class MessageDTO {
    private Long senderId;
    private Long chatId;
    private String messageContent;

    public MessageDTO() {
    }

    public MessageDTO(Long senderId, Long chatId, String messageContent) {
        this.senderId = senderId;
        this.chatId = chatId;
        this.messageContent = messageContent;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    @Override
    public String toString() {
        return "MessageDTO{" + "senderId=" + senderId + ", chatId=" + chatId + ", messageContent='" + messageContent + '\'' + '}';
    }
}