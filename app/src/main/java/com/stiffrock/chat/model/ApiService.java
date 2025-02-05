package com.stiffrock.chat.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("mensajes/enviar")
    Call<Mensaje> enviarMensaje(@Body Mensaje mensaje);

    @GET("mensajes/recibir/{usuario}")
    Call<List<Mensaje>> obtenerMensajes(@Path("usuario") String usuario);
}
