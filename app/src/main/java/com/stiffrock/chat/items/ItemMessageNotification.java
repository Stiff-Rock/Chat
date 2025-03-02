package com.stiffrock.chat.items;

/**
 * Elemento de RecyclerView que representa a un mensaje de notificación del servidor en el chat
 * <p>
 * Extiende de {@link Item}
 * </p>
 * Atributos:
 * <p>
 * - {@link #message}: Contenido en texto de la notificación
 */
public class ItemMessageNotification extends Item {
    private final String message;

    public ItemMessageNotification(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int getType() {
        return 4;
    }
}
