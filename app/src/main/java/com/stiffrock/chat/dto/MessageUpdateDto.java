package com.stiffrock.chat.dto;

import androidx.annotation.NonNull;

import com.stiffrock.chat.model.Message;
import com.stiffrock.chat.model.MessageState;
import com.stiffrock.chat.model.User;

/**
 * Data Tansfer Object para estandarizar la serialización de las solicitudes de actualización
 * de mensajes.
 */
public class MessageUpdateDto {
    private Long readerUser;
    private Long msgId;
    private MessageState state;

    public MessageUpdateDto() {
    }

    public MessageUpdateDto(User readerUser, Message msgId, MessageState state) {
        this.readerUser = readerUser.getId();
        this.msgId = msgId.getId();
        this.state = state;
    }

    public Long getReaderUser() {
        return readerUser;
    }

    public void setReaderUser(Long readerUser) {
        this.readerUser = readerUser;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public MessageState getState() {
        return state;
    }

    public void setState(MessageState state) {
        this.state = state;
    }

    @NonNull
    @Override
    public String toString() {
        return "MessageUpdateDto{" + "readerUser=" + readerUser + ", msgId=" + msgId + ", state=" + state + '}';
    }
}