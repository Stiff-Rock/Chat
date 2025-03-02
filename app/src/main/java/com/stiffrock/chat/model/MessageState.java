package com.stiffrock.chat.model;

/**
 * Representa los diferentes estados en los que un mensaje puede encontrarse.
 * <p>
 * - SENT: El mensaje ha sido enviado correctamente, pero aún no ha sido leído por el destinatario.
 * <p>
 * - PARTIALLY_READ: El mensaje ha sido parcialmente leído, por ejemplo, cuando el usuario ha visto
 * una vista previa o ha leído solo una parte del mensaje.
 * <p>
 * - READ: El mensaje ha sido completamente leído por el destinatario.
 * <p>
 * Un mensaje puede contener el estado "null", que representa que no se ha enviado siquiera al servidor.
 */
public enum MessageState {
    SENT, PARTIALLY_READ, READ
}
