package com.stiffrock.chat.dto;

/**
 * Data Tansfer Object para estandarizar la serialización de respuestas del servidor que contengan
 * mensajes en formato texto
 */
public class ApiResponse {
    private String message;

    public ApiResponse() {
    }

    public ApiResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
