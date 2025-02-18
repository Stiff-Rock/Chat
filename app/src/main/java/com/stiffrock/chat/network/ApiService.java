package com.stiffrock.chat.network;

import com.stiffrock.chat.model.ApiResponse;
import com.stiffrock.chat.model.Chat;
import com.stiffrock.chat.model.Message;
import com.stiffrock.chat.model.User;

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

    @GET("messages/recieve/{recipient}")
    Call<List<Message>> recieveMessage(@Path("recipient") String usuario);

    //Endpoint de usuarios
    @POST("users/register")
    Call<ApiResponse> registerUser(@Body User user);

    @POST("users/login")
    Call<ApiResponse> logInUser(@Body User user);

    @GET("users/online")
    Call<List<User>> getOnlineUsers();

    //Endpoint de grupos
    @POST("groups/create")
    Call<Chat> createGroupChat();
}
