package com.stiffrock.chat.items;

/**
 * Elemento de RecyclerView que representa a un mensaje de recibido en el chat
 * <p>
 * Extiende de {@link Item}
 * </p>
 * Atributos:
 * <p>
 * - {@link #sender}: Nombre del usuario que a enviado el mensaje
 * <p>
 * - {@link #message}: Contenido en texto del mensaje
 * <p>
 * - {@link #timestamp}: Hora a la que se envió el mensaje
 */
public class ItemMessageRecieved extends Item {
    private final String sender;
    private String message;
    private final String timestamp;

    public ItemMessageRecieved(String sender, String message, String timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
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

    @Override
    public int getType() {
        return 0;
    }
}
