package com.stiffrock.chat.fragments.chat;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stiffrock.chat.R;
import com.stiffrock.chat.adapters.MyAdapter;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemMessageNotification;
import com.stiffrock.chat.items.ItemMessageRecieved;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.Message;
import com.stiffrock.chat.model.PrivateChat;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoFragment extends Fragment {
    private final User user;

    private LinearLayout loadingScreen;

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<Item> messageItems;

    private ApiService apiService;

    public UserInfoFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_info, container, false);

        apiService = RetrofitClient.getApiService();

        loadingScreen = view.findViewById(R.id.loadingScreen);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        TextView tvChatName = view.findViewById(R.id.tvChatName);
        tvChatName.setText(user.getUsername());

        TextView tvIsContact = view.findViewById(R.id.tvIsContact);
        String isInContactsMsg = "No esta en tu lista de contactos";
        if (!user.equals(CurrentUser.getCurrentUser()))
            for (PrivateChat pc : CurrentUser.getContacts()) {
                if (pc.getContact(CurrentUser.getCurrentUser()).equals(user)) {
                    isInContactsMsg = "Está en tu lista de contactos";
                    break;
                }
            }
        else isInContactsMsg = "(Tú)";
        tvIsContact.setText(isInContactsMsg);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        apiGetUserMessages();
    }

    private void apiGetUserMessages() {
        Call<List<Message>> call = apiService.getUserMessages(user.getId());
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Message> msgs = response.body();
                    messageItems = new ArrayList<>();
                    for (Message msg : msgs) {
                        String sender = msg.getSender().getUsername();
                        String message = msg.getMessageContent();
                        String timestamp = formatDateTime(msg.getTimestamp());
                        messageItems.add(new ItemMessageRecieved(sender, message, timestamp));
                    }

                    if (messageItems.isEmpty()) {
                        String notification = user.getUsername() + " no ha enviado mensajes";
                        messageItems.add(new ItemMessageNotification(notification));
                    }

                    adapter = new MyAdapter(messageItems);
                    recyclerView.setAdapter(adapter);
                    loadingScreen.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    messageItems = new ArrayList<>();
                    String notification = "No se han podido obtener los mensajes enviados por " + user.getUsername();
                    messageItems.add(new ItemMessageNotification(notification));
                    adapter = new MyAdapter(messageItems);
                    recyclerView.setAdapter(adapter);
                    Log.e(TAG, "Error sending message: " + response.code());
                    loadingScreen.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable throwable) {
                messageItems = new ArrayList<>();
                String notification = "No se han podido obtener los mensajes enviados por " + user.getUsername();
                messageItems.add(new ItemMessageNotification(notification));
                adapter = new MyAdapter(messageItems);
                recyclerView.setAdapter(adapter);
                loadingScreen.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                Log.e(TAG, "GetUserMessages request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDateTime(LocalDateTime now) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return now.format(formatter);
    }
}