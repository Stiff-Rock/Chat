package com.stiffrock.chat.model;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Clase que representa un mensaje enviado por un usuario o como notificacion del servidor.
 * </p>
 * Atributos:
 * <p>
 * - {@link #id}: Id único del mensaje en la BBDD.
 * <p>
 * - {@link #sender}: Referencia al objeto {@link User} del usuario que ha enviado el mensaje.
 * <p>
 * - {@link #chat}: Referencia al objeto {@link BaseChat} del chat al que que se ha enviado el mensaje.
 * <p>
 * - {@link #messageContent}: Contenido del mensaje.
 * <p>
 * - {@link #timestamp}: Hora a la que se ha enviado el mensaje.
 * <p>
 * - {@link #messageState}: Estado en el que se encuentra el mensaje.
 * <p>
 * - {@link #readBy}: Set de usuarios que han leido el mensaje.
 * <p>
 * - {@link #deleted}: Boolean que indica si el usuario ha "eliminado" el mensaje. El mensaje no se
 * elimina realmente de la BBDD pero evita que se muestre el contenido del mensaje en el chat, mostrando
 * un aviso de 'Mensaje eliminado' en vez de el contenido oringial.
 */
public class Message {
    private Long id;
    private User sender;
    private BaseChat chat;
    private String messageContent;
    private LocalDateTime timestamp;
    private MessageState messageState;
    private Set<User> readBy = new HashSet<>();
    private boolean deleted = false;

    public Message() {
    }

    public Message(User sender, BaseChat chat, String messageContent, LocalDateTime timestamp) {
        this.sender = sender;
        this.chat = chat;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
        messageState = MessageState.SENT;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public BaseChat getChat() {
        return chat;
    }

    public void setChat(BaseChat groupChat) {
        this.chat = groupChat;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public MessageState getMessageState() {
        return messageState;
    }

    public void setMessageState(MessageState messageState) {
        this.messageState = messageState;
    }

    public Set<User> getReadBy() {
        return readBy;
    }

    public void setReadBy(Set<User> readBy) {
        this.readBy = readBy;
    }

    public void markMsgReadByUser(User user) {
        readBy.add(user);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "Message{" + "id=" + id + ", sender=" + sender + ", chat=" + chat + ", messageContent='" + messageContent + '\'' + ", timestamp=" + timestamp + ", messageState=" + messageState + ", deleted=" + deleted + '}';
    }
}
