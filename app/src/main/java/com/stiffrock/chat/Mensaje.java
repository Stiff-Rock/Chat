package com.stiffrock.chat;

import java.time.LocalDateTime;

public class Mensaje {
    private Long id;
    private String remitente;
    private String destinatario;
    private String mensaje;
    private LocalDateTime timeStamp;

    public Mensaje(Long id, String remitente, String destinatario, String mensaje, LocalDateTime timeStamp) {
        this.id = id;
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.mensaje = mensaje;
        this.timeStamp = timeStamp;
    }

    public Long getId() {
        return id;
    }

    public String getRemitente() {
        return remitente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
}
