package com.stiffrock.chat.fragments.home;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stiffrock.chat.ChatActivity;
import com.stiffrock.chat.HomeActivity;
import com.stiffrock.chat.R;
import com.stiffrock.chat.adapters.MyAdapter;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemChatCard;
import com.stiffrock.chat.model.BaseChat;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.network.WebSocketClient;
import com.stiffrock.chat.utils.OnItemClickListener;
import com.stiffrock.chat.utils.WebSocketNotificationListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ContactsFragment extends Fragment implements OnItemClickListener, WebSocketNotificationListener {
    private ApiService apiService;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<Item> chats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        CurrentUser.setCurrentChat(null);

        apiService = RetrofitClient.getApiService();
        WebSocketClient.getInstance().setOnNotificationReceivedListener(this);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        Log.e(TAG, "LOADED");
        chats = new ArrayList<>();
        apiGetChatList();

        return view;
    }

    private void apiGetChatList() {
        Call<List<BaseChat>> call = apiService.getUserChats(CurrentUser.getCurrentUser().getId());
        call.enqueue(new Callback<List<BaseChat>>() {
            @Override
            public void onResponse(@NonNull Call<List<BaseChat>> call, @NonNull Response<List<BaseChat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (BaseChat chat : response.body()) {
                        Log.e(TAG, chat.getId().toString());
                        chats.add(new ItemChatCard(chat));
                    }
                } else {
                    Toast.makeText(requireContext(), "No se han encontrado contactos", Toast.LENGTH_SHORT).show();
                }

                adapter = new MyAdapter(chats, ContactsFragment.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<BaseChat>> call, @NonNull Throwable throwable) {
                Log.e(TAG, "GetUserChats request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();

                adapter = new MyAdapter(chats, ContactsFragment.this);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    public void addContact(BaseChat chat) {
        chats.add(new ItemChatCard(chat));
        adapter.notifyItemInserted(chats.size() - 1);
    }

    @Override
    public void onItemClick(ItemChatCard chat) {
        CurrentUser.setCurrentChat(chat.getChat());
        ((HomeActivity) requireActivity()).navigateToActivity(ChatActivity.class);
    }

    private void apiGetChat(Long chatId) {
        Call<BaseChat> call = apiService.getChat(chatId);
        call.enqueue(new Callback<BaseChat>() {
            @Override
            public void onResponse(@NonNull Call<BaseChat> call, @NonNull Response<BaseChat> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseChat chat = response.body();
                    Log.d(TAG, "CHAT RECIEVED: " + chat);
                    chats.add(new ItemChatCard(chat));
                    adapter.notifyItemInserted(chats.size() - 1);
                    Toast.makeText(requireContext(), "Se ha añadido un nuevo chat", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Error getting chat");
                    Toast.makeText(requireContext(), "No se ha podido obtener el chat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseChat> call, @NonNull Throwable throwable) {
                Log.e(TAG, "GetChat request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void apiDeleteChat(Long chatId) {
        //TODO
        Log.w(TAG, "IMPLEMENT DELETING CHATS: " + chatId);
    }

    //TODO: CREATOR RECIEVES AGAIN THE CHAT
    @Override
    public void onNotificationReceived(String notification) {
        JsonObject json = JsonParser.parseString(notification).getAsJsonObject();
        String action = json.get("action").getAsString();
        Long chatId = json.get("chatId").getAsLong();
        if (action.equals("ADD")) {
            apiGetChat(chatId);
        } else if (action.equals("DELETE")) {
            apiDeleteChat(chatId);
        }
    }
}