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

import com.stiffrock.chat.ChatActivity;
import com.stiffrock.chat.HomeActivity;
import com.stiffrock.chat.R;
import com.stiffrock.chat.adapters.MyAdapter;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemChatCard;
import com.stiffrock.chat.model.Chat;
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
    private final List<Item> chats = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        CurrentUser.setCurrentChat(null);

        apiService = RetrofitClient.getApiService();
        WebSocketClient.getInstance().setOnNotificationReceivedListener(this);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        apiGetChatList();

        return view;
    }

    private void apiGetChatList() {
        Call<List<Chat>> call = apiService.getUserChats(CurrentUser.getCurrentUser().getId());
        call.enqueue(new Callback<List<Chat>>() {
            @Override
            public void onResponse(@NonNull Call<List<Chat>> call, @NonNull Response<List<Chat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Chat chat : response.body()) {
                        //TODO: MAKE SEPPARATE CONTACT AND GROUP CLASSES
                        chats.add(new ItemChatCard(chat));
                    }
                } else {
                    Toast.makeText(requireContext(), "No se han encontrado contactos", Toast.LENGTH_SHORT).show();
                }

                adapter = new MyAdapter(chats, ContactsFragment.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<Chat>> call, @NonNull Throwable throwable) {
                Log.e(TAG, "GetUserChats request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();

                adapter = new MyAdapter(chats, ContactsFragment.this);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    public void addContact(Chat chat) {
        chats.add(new ItemChatCard(chat));
        adapter.notifyItemInserted(chats.size() - 1);
    }

    @Override
    public void onItemClick(ItemChatCard chat) {
        CurrentUser.setCurrentChat(chat.getChat());
        ((HomeActivity) requireActivity()).navigateToActivity(ChatActivity.class);
    }

    private void apiGetChat(Long chatId) {
        Call<Chat> call = apiService.getChat(chatId);
        call.enqueue(new Callback<Chat>() {
            @Override
            public void onResponse(@NonNull Call<Chat> call, @NonNull Response<Chat> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Chat chat = response.body();
                    chats.add(new ItemChatCard(chat));
                    adapter.notifyItemInserted(chats.size() - 1);
                    Toast.makeText(requireContext(), "Se ha añadido un nuevo chat", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Error getting chat");
                    Toast.makeText(requireContext(), "No se ha podido obtener el chat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Chat> call, @NonNull Throwable throwable) {
                Log.e(TAG, "GetChat request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void apiDeleteChat(Long chatId) {

    }

    @Override
    public void onNotificationReceived(String notification) {
        //TODO: LOOK FOR ANOTHER WAY TO DO THIS
        Log.e(TAG, notification);
        String[] query = notification.split(":");
        String action = query[0];
        Long chatId = Long.valueOf(query[1]);
        if (action.equals("ADD")) {
            apiGetChat(chatId);
        } else {
            apiDeleteChat(chatId);
        }
    }
}