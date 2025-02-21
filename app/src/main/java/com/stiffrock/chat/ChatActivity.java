package com.stiffrock.chat;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stiffrock.chat.adapters.MyAdapter;
import com.stiffrock.chat.dto.MessageDTO;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemMessageRecieved;
import com.stiffrock.chat.items.ItemMessageSent;
import com.stiffrock.chat.model.BaseChat;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.GroupChat;
import com.stiffrock.chat.model.Message;
import com.stiffrock.chat.model.PrivateChat;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.network.WebSocketClient;
import com.stiffrock.chat.utils.BaseChatTypeAdapter;
import com.stiffrock.chat.utils.GsonManager;
import com.stiffrock.chat.utils.WebSocketNotificationListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements WebSocketNotificationListener {
    private final List<Item> msgItems = new ArrayList<>();
    private final Set<Message> messages = new HashSet<>();

    private EditText etMensaje;

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        BaseChat chat = CurrentUser.getCurrentChat();
        toolbar.setTitle(getChatName(chat));
        setSupportActionBar(toolbar);

        apiService = RetrofitClient.getApiService();

        etMensaje = findViewById(R.id.etMensaje);

        findViewById(R.id.sendText).setOnClickListener(e -> sendMessage());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyAdapter(msgItems);

        getMessageHistory();

        recyclerView.setAdapter(adapter);

        WebSocketClient.getInstance().setOnNotificationReceivedListener(this);
    }

    private void getMessageHistory() {
        Long chatId = CurrentUser.getCurrentChat().getId();
        Call<List<Message>> call = apiService.getMessageHistory(chatId);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Message> msgHistory = response.body();
                    for (Message msg : msgHistory) {
                        if (!messages.contains(msg)) {
                            messages.add(msg);
                            User sender = msg.getSender();
                            int type = sender.equals(CurrentUser.getCurrentUser()) ? 1 : 2;
                            addTextBubble(type, msg.getMessageContent());
                        }
                    }
                } else {
                    Log.e(TAG, "Error loading message history");
                    Toast.makeText(ChatActivity.this, "Error cargando historial de mensajes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable throwable) {
                Log.e(TAG, "GetMessageHistory request failed: " + throwable.getMessage());
                Toast.makeText(ChatActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addTextBubble(int itemType, String text) {
        if (itemType == 1) {
            msgItems.add(new ItemMessageSent(text));
        } else if (itemType == 2) {
            msgItems.add(new ItemMessageRecieved(text));
        }

        adapter.notifyItemInserted(msgItems.size() - 1);
        recyclerView.scrollToPosition(msgItems.size() - 1);
    }

    private void sendMessage() {
        String text = etMensaje.getText().toString().trim();
        if (text.isBlank()) return;
        etMensaje.setText("");
        addTextBubble(1, text);
        Long userId = CurrentUser.getCurrentUser().getId();
        Long chatID = CurrentUser.getCurrentChat().getId();
        apiSendMessage(new MessageDTO(userId, chatID, text));
    }

    private void recieveMessage(Message msg) {
        messages.add(msg);

        BaseChat currentChat = CurrentUser.getCurrentChat();
        BaseChat msgChat = msg.getChat();

        Long chatId = msgChat.getId();
        Long currentChatId = currentChat.getId();

        if (chatId.equals(currentChatId)) addTextBubble(2, msg.getMessageContent());
        else showNotification(msg);
    }

    //TODO: HANDLE FALIED CONNECTIONS
    private void apiSendMessage(MessageDTO messageDTO) {
        Call<Message> call = apiService.sendMessage(messageDTO);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Message msg = response.body();
                    messages.add(msg);
                    Log.d(TAG, "Message sent: " + msg);
                } else {
                    Log.e(TAG, "Error sending message: " + response.code());
                    Toast.makeText(ChatActivity.this, "Error enviado el mensaje", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Log.e(TAG, "SendMesssage request failed: " + t.getMessage());
                Toast.makeText(ChatActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //TODO: TEST
    private void showNotification(Message msg) {
        BaseChat chat = msg.getChat();
        Toast.makeText(this, "Mensaje recibido de " + getChatName(chat), Toast.LENGTH_SHORT).show();
    }

    private String getChatName(BaseChat chat) {
        String name;
        if (chat instanceof PrivateChat) name = ((PrivateChat) chat).getName();
        else name = ((GroupChat) chat).getName();
        String[] usernames = name.split("&");
        String currentUsrName = CurrentUser.getCurrentUser().getUsername();
        return usernames[0].equals(currentUsrName) ? usernames[1] : usernames[0];
    }

    //TODO: QUIZAS ESTO TAMBIEN EN EL CONTANCTFRAGMENT CON LA NOTIFICACION DE TOAST
    @Override
    public void onNotificationReceived(String notification) {
        Log.w(TAG, "WEBSOCKETMESSAGE: " + notification);
        //TODO: ADAPTAR PARA QUE REIBA OTRO DIPO DE MENSAJES TAMBIEN
        Message msg = GsonManager.gson.fromJson(notification, Message.class);
        Log.w(TAG, "MSG: " + msg);
        recieveMessage(msg);
    }
}