package com.stiffrock.chat.utils;

/**
 * Interfaz para gestionar la recpción de notificaiones por parte del WebSocket
 */
public interface WebSocketNotificationListener {

    /**
     * Método que se llama cuando el WebSocket envía una notificacion.
     *
     * @param notification Contenido de la notifición en formato JSON
     */
    void onNotificationReceived(String notification);
}
