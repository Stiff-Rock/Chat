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
import com.stiffrock.chat.model.BaseChat;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.GroupChat;
import com.stiffrock.chat.model.Message;
import com.stiffrock.chat.model.PrivateChat;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.network.WebSocketClient;
import com.stiffrock.chat.network.WebSocketNotification;
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

        apiService = RetrofitClient.getApiService();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        CurrentUser.setCurrentChat(null);
        WebSocketClient.getInstance().setOnNotificationReceivedListener(this);
        apiGetChatList();
    }

    private void apiGetChatList() {
        chats = new ArrayList<>();
        Call<List<BaseChat>> call = apiService.getUserChats(CurrentUser.getCurrentUser().getId());
        call.enqueue(new Callback<List<BaseChat>>() {
            @Override
            public void onResponse(@NonNull Call<List<BaseChat>> call, @NonNull Response<List<BaseChat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (BaseChat chat : response.body()) {
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
    public void onItemClick(ItemChatCard itemChat) {
        CurrentUser.setCurrentChat(itemChat.getChat());
        ((HomeActivity) requireActivity()).navigateToActivity(ChatActivity.class);
    }

    private void addChat(BaseChat chat) {
        chats.add(new ItemChatCard(chat));
        adapter.notifyItemInserted(chats.size() - 1);
        Toast.makeText(requireContext(), "Se ha añadido un nuevo chat", Toast.LENGTH_SHORT).show();
    }

    private void deleteChat(BaseChat chat) {
        //TODO
        Log.w(TAG, "IMPLEMENT DELETING CHATS: " + chat);
    }

    private void showNotification(Message msg) {
        BaseChat chat = msg.getChat();
        Toast.makeText(requireContext(), "Mensaje recibido de " + getChatName(chat), Toast.LENGTH_SHORT).show();
    }

    private String getChatName(BaseChat chat) {
        String name;
        if (chat instanceof PrivateChat) name = ((PrivateChat) chat).getName();
        else name = ((GroupChat) chat).getName();
        String[] usernames = name.split("&");
        String currentUsrName = CurrentUser.getCurrentUser().getUsername();
        return usernames[0].equals(currentUsrName) ? usernames[1] : usernames[0];
    }

    //TODO: CREATOR RECIEVES AGAIN THE CHAT
    @Override
    public void onNotificationReceived(String notification) {
        WebSocketNotification wsn = WebSocketNotification.parse(notification);
        switch (wsn.getAction()) {
            case MESSAGE_RECEIVED:
                Message msg = (Message) wsn.getContent();
                showNotification(msg);
                break;
            case ADD_CONTACT:
                BaseChat addChat = (BaseChat) wsn.getContent();
                addChat(addChat);
                break;
            case DELETE_CONTACT:
                BaseChat deleteChat = (BaseChat) wsn.getContent();
                deleteChat(deleteChat);
                break;
            default:
                break;
        }
    }
}