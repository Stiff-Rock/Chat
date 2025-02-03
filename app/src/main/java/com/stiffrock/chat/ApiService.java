package com.stiffrock.chat;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("chat/enviar")
    Call<Mensaje> enviarMensaje(@Body Mensaje mensaje);

    @GET("chat/mensajes/{usuario}")
    Call<List<Mensaje>> obtenerMensajes(@Path("usuario") String usuario);
}
