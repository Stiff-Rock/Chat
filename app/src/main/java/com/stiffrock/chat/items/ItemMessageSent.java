package com.stiffrock.chat.items;

import com.stiffrock.chat.model.MessageState;

/**
 * Elemento de RecyclerView que representa a un mensaje de enviado en el chat
 * <p>
 * Extiende de {@link Item}
 * </p>
 * Atributos:
 * <p>
 * - {@link #sender}: Nombre del usuario que a enviado el mensaje (El usuario utilizando la app)
 * <p>
 * - {@link #message}: Contenido en texto del mensaje
 * <p>
 * - {@link #timestamp}: Hora a la que se envió el mensaje
 * <p>
 * - {@link #messageState}: Estado en el que se encuentra el mensaje (No enviado, enviado, leido-parcial, leido por todos)
 */
public class ItemMessageSent extends Item {
    private final String sender;
    private String message;
    private final String timestamp;
    private MessageState messageState;

    public ItemMessageSent(String sender, String message, String timestamp, MessageState messageState) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.messageState = messageState;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageState getMessageState() {
        return messageState;
    }

    public void setMessageState(MessageState messageState) {
        this.messageState = messageState;
    }

    @Override
    public int getType() {
        return 1;
    }
}
