package com.stiffrock.chat.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Data Tansfer Object para estandarizar la serialización las respuestas de las solicitudes de la
 * subida de archivos de imagen.
 */
public class UploadResponse {
    @SerializedName("url")
    private String url;

    public String getUrl() {
        return url;
    }
}