package com.stiffrock.chat.network;

import com.stiffrock.chat.dto.ApiResponse;
import com.stiffrock.chat.dto.CreateChatDTO;
import com.stiffrock.chat.dto.CredentialsDTO;
import com.stiffrock.chat.dto.MessageDTO;
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

    // Endpoint de mensajes
    @POST("messages/send")
    Call<Message> sendMessage(@Body MessageDTO messageDTO);

    @GET("messages/recieve/{messageId}")
    Call<Message> recieveMessage(@Path("messageId") Long messageId);


    // Endpoint de usuarios
    @POST("users/register")
    Call<ApiResponse> registerUser(@Body CredentialsDTO credentials);

    @POST("users/login")
    Call<User> logInUser(@Body CredentialsDTO credentials);

    @GET("users/online")
    Call<List<User>> getOnlineUsers();

    @GET("users/user/{username}")
    Call<User> getUserByUsername(@Path("username") String username);

    @GET("users/user/{userId}/chats")
    Call<List<Chat>> getUserChats(@Path("userId") Long userId);


    // Endpoint de chats
    @POST("chats/create")
    Call<Chat> addContact(@Body CreateChatDTO createChatDTO);

    @GET("chats/chat/{chatId}")
    Call<Chat> getChat(@Path("chatId") Long chatId);


    // Endpoint de grupos
    @POST("groups/create")
    Call<ApiResponse> createGroupChat(@Body CreateChatDTO createChatDTO);
}
