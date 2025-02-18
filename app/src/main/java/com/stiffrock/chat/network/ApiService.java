package com.stiffrock.chat.network;

import com.stiffrock.chat.dto.CreateGroupDTO;
import com.stiffrock.chat.dto.LoginDTO;
import com.stiffrock.chat.model.ApiResponse;
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
    Call<ApiResponse> registerUser(@Body LoginDTO credentials);

    @POST("users/login")
    Call<User> logInUser(@Body LoginDTO credentials);

    @GET("users/online")
    Call<List<User>> getOnlineUsers();

    @GET("users/{username}")
    Call<User> getUserByUsername(@Path("username") String username);

    //Endpoint de grupos
    @POST("groups/create")
    Call<ApiResponse> createGroupChat(@Body CreateGroupDTO request);
}
