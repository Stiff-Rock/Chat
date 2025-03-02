package com.stiffrock.chat.model;

/**
 * Representa las diferentes acciones que pueden ser notificadas a través de WebSocket.
 * <p>
 * - ADD_CHAT: Acción que indica la creación de un nuevo chat.
 * <p>
 * - DELETE_CONTACT: Acción para eliminar un contacto.
 * <p>
 * - DELETE_GROUP: Acción para eliminar un chat grupal.
 * <p>
 * - MESSAGE_RECEIVED: Indica que se ha recibido un mensaje.
 * <p>
 * - MESSAGE_READ: Indica que un mensaje ha sido leído por algun participante del chat.
 * <p>
 * - MESSAGE_DELETED: Indica que un mensaje ha sido eliminado.
 * <p>
 * - USER_CONNECTED: Notifica que un usuario se ha conectado al sistema.
 * <p>
 * - USER_DISCONNECTED: Notifica que un usuario se ha desconectado del sistema.
 * <p>
 * - USER_CONNECTED_TO_GROUP_CHAT: Indica que un usuario se ha conectado a un chat grupal.
 * <p>
 * - USER_DISCONNECTED_FROM_GROUP_CHAT: Indica que un usuario ha desconectado de un chat grupal.
 * <p>
 * - GROUP_CHAT_CHANGED: Notifica que ha habido cambios en los miembros de un chat grupal.
 * <p>
 * - GROUP_CHAT_DELETION: Notifica la eliminación de un miembro de un chat grupal.
 * <p>
 * - GET_USER_ONLINE_STATUS: Acción para solicitar el estado en línea de un usuario específico.
 * <p>
 * - GET_CONTACTS_ONLINE_STATUS: Acción para solicitar el estado en línea de los contactos del usuario.
 * <p>
 * - ERROR: Indica que se ha producido un error en la comunicación o en la acción solicitada.
 */
public enum WebSocketAction {
    ADD_CHAT,
    DELETE_CONTACT,
    DELETE_GROUP,

    MESSAGE_RECEIVED,
    MESSAGE_READ,
    MESSAGE_DELETED,

    USER_CONNECTED,
    USER_DISCONNECTED,

    USER_CONNECTED_TO_GROUP_CHAT,
    USER_DISCONNECTED_FROM_GROUP_CHAT,

    GROUP_CHAT_CHANGED,
    GROUP_CHAT_DELETION,

    GET_USER_ONLINE_STATUS,
    GET_CONTACTS_ONLINE_STATUS,

    ERROR
}
