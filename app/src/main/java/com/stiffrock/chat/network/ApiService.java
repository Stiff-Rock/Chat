package com.stiffrock.chat.network;

import com.stiffrock.chat.dto.ApiResponse;
import com.stiffrock.chat.dto.CredentialsDTO;
import com.stiffrock.chat.dto.GroupChatDTO;
import com.stiffrock.chat.dto.MessageDTO;
import com.stiffrock.chat.dto.PrivateChatDTO;
import com.stiffrock.chat.model.BaseChat;
import com.stiffrock.chat.model.GroupChat;
import com.stiffrock.chat.model.Message;
import com.stiffrock.chat.model.PrivateChat;
import com.stiffrock.chat.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // Endpoint de mensajes
    @POST("messages/send")
    Call<Message> sendMessage(@Body MessageDTO messageDTO);

    @GET("messages/history/{chatId}")
    Call<List<Message>> getMessageHistory(@Path("chatId") Long chatId);

    @GET("messages/private/{userId}")
    Call<List<Message>> getUserMessages(@Path("userId") Long userId);

    @PUT("messages/message/{messageId}")
    Call<ApiResponse> deleteMessage(@Path("messageId") Long messageId);

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
    Call<List<BaseChat>> getUserChats(@Path("userId") Long userId);


    // Endpoint de chats
    @POST("chats/private/create")
    Call<PrivateChat> addContact(@Body PrivateChatDTO privateChatDTO);
    //TODO USELESS?
    @GET("chats/chat/{chatId}")
    Call<BaseChat> getChat(@Path("chatId") Long chatId);


    // Endpoint de grupos
    @POST("groups/create")
    Call<ApiResponse> createGroupChat(@Body GroupChatDTO groupChatDTO);

    @GET("groups/group/{groupId}")
    Call<GroupChat> getGroupChat(@Path("groupId") Long groupId);

    @POST("groups/{groupId}/members/{userId}")
    Call<ApiResponse> addMember(@Path("groupId") Long groupId, @Path("userId") Long userId);

    @DELETE("groups/{groupId}/members/{userId}")
    Call<ApiResponse> removeMember(@Path("groupId") Long groupId, @Path("userId") Long userId);

    @POST("groups/{groupId}/admins/{userId}")
    Call<ApiResponse> addAdmin(@Path("groupId") Long groupId, @Path("userId") Long userId);

    @DELETE("groups/{groupId}/admins/{userId}")
    Call<ApiResponse> removeAdmin(@Path("groupId") Long groupId, @Path("userId") Long userId);
}
