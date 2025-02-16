package com.stiffrock.chat.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    //Endpoint de mensajes
    @POST("messages/send")
    Call<Message> sendMessage(@Body Message message);

    @GET("messages/recieve")
    Call<List<Message>> recieveMessage(@Path("recipient") String usuario);

    //Endpoint de usuarios
    @POST("users/register")
    Call<Message> registerUser(@Body Message message);

    @POST("users/login/{usuario}")
    Call<List<Message>> logInUser(@Path("usuario") String usuario);

    //Endpoint de grupos
}
