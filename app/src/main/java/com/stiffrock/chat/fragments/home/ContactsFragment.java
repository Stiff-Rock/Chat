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
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.model.WebSocketAction;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.network.WebSocketClient;
import com.stiffrock.chat.network.WebSocketNotification;
import com.stiffrock.chat.utils.GsonManager;
import com.stiffrock.chat.utils.OnItemClickListener;
import com.stiffrock.chat.utils.WebSocketNotificationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsFragment extends Fragment implements OnItemClickListener, WebSocketNotificationListener {
    private ApiService apiService;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<Item> chats;
    public Map<User, ItemChatCard> userChatMap;

    private WebSocketClient wsClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        apiService = RetrofitClient.getApiService();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        wsClient = WebSocketClient.getInstance();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        CurrentUser.setCurrentChat(null);
        wsClient.setOnNotificationReceivedListener(this);
        apiGetChatList();
    }

    //TODO: ACTUALIZAR SOLO LOS QUE NO ESTEN??
    private void apiGetChatList() {
        chats = new ArrayList<>();
        userChatMap = new HashMap<>();
        Call<List<BaseChat>> call = apiService.getUserChats(CurrentUser.getCurrentUser().getId());
        call.enqueue(new Callback<List<BaseChat>>() {
            @Override
            public void onResponse(@NonNull Call<List<BaseChat>> call, @NonNull Response<List<BaseChat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (BaseChat chat : response.body()) {
                        ItemChatCard icc = new ItemChatCard(chat);
                        chats.add(icc);
                        if (chat instanceof PrivateChat) {
                            User contact = ((PrivateChat) chat).getContact(CurrentUser.getCurrentUser());
                            userChatMap.put(contact, icc);
                            CurrentUser.getContacts().add((PrivateChat) chat);
                        }
                    }
                    wsGetContactsStatus();
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

    private void wsGetContactsStatus() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", WebSocketAction.GET_CONTACTS_ONLINE_STATUS.name());
        String currentUserJson = GsonManager.gson.toJson(CurrentUser.getCurrentUser());
        jsonObject.add("content", GsonManager.gson.fromJson(currentUserJson, JsonObject.class));
        wsClient.sendMessage(jsonObject.toString());
    }

    public void addContact(BaseChat chat, User user) {
        ItemChatCard icc = new ItemChatCard(chat);
        chats.add(icc);
        userChatMap.put(user, icc);
        adapter.notifyItemInserted(chats.size() - 1);
        //TODO GET SINGLE CONTACT STATUS NOT ALL
        wsGetContactsStatus();
    }

    private void addChat(BaseChat chat) {
        chats.add(new ItemChatCard(chat));
        adapter.notifyItemInserted(chats.size() - 1);
        Toast.makeText(requireContext(), "Se ha añadido un nuevo chat", Toast.LENGTH_SHORT).show();
        if (chat instanceof PrivateChat) wsGetContactsStatus();
    }

    private void deleteChat(BaseChat chat) {
        //TODO Delete chat
        Log.w(TAG, "IMPLEMENT DELETING CHATS: " + chat);
    }

    //TODO: MAKE UNREAD MESSAGES BUBBLE
    private void showNotification(Message msg) {
        if (msg.getSender().getUsername().equals("SYSTEM")) return;

        BaseChat chat = msg.getChat();
        Toast.makeText(requireContext(), "Mensaje recibido de " + getChatName(chat), Toast.LENGTH_SHORT).show();

        ItemChatCard icc = userChatMap.get(msg.getSender());
        int lastIndex = chats.indexOf(icc);

        if (lastIndex == 0) return;

        boolean isDeleted = chats.remove(icc);
        if (!isDeleted) {
            Log.e(TAG, "Could not delete chatCard for user <" + msg.getSender().getUsername() + ">");
            return;
        }
        chats.add(0, icc);
        adapter.notifyItemMoved(lastIndex, 0);
    }

    private String getChatName(BaseChat chat) {
        String name;
        if (chat instanceof PrivateChat) name = ((PrivateChat) chat).getName();
        else name = ((GroupChat) chat).getName();
        String[] usernames = name.split("&");
        String currentUsrName = CurrentUser.getCurrentUser().getUsername();
        return usernames[0].equals(currentUsrName) ? usernames[1] : usernames[0];
    }

    private void updateContactOnlineStatus(boolean isOnline, User user) {
        ItemChatCard icc = userChatMap.get(user);
        if (icc == null) {
            Log.w(TAG, "Could not retireve contact ChatCard:\nUser: " + user);
            return;
        }
        icc.setOnline(isOnline);
        int index = chats.indexOf(icc);
        adapter.notifyItemChanged(index);
    }

    @Override
    public void onItemClick(View view, Item item, int postion) {
        CurrentUser.setCurrentChat(((ItemChatCard) item).getChat());
        ((HomeActivity) requireActivity()).navigateToActivity(ChatActivity.class);
    }

    @Override
    public void onLongItemClick(View view, Item item, int postion) {
    }

    @Override
    public void onNotificationReceived(String notification) {
        WebSocketNotification wsn = WebSocketNotification.parse(notification);
        switch (wsn.getAction()) {
            case MESSAGE_RECEIVED:
                Message msg = (Message) wsn.getContent();
                showNotification(msg);
                break;
            case ADD_CHAT:
                BaseChat addChat = (BaseChat) wsn.getContent();
                addChat(addChat);
                break;
            case DELETE_CONTACT:
                BaseChat deleteChat = (BaseChat) wsn.getContent();
                deleteChat(deleteChat);
                break;
            case USER_CONNECTED:
                User userConnected = (User) wsn.getContent();
                updateContactOnlineStatus(true, userConnected);
                break;
            case USER_DISCONNECTED:
                User userDisconnected = (User) wsn.getContent();
                updateContactOnlineStatus(false, userDisconnected);
                break;
            default:
                break;
        }
    }
}