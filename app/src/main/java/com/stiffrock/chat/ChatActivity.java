package com.stiffrock.chat;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemMessageRecieved;
import com.stiffrock.chat.items.ItemMessageSent;
import com.stiffrock.chat.model.ApiService;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.Message;
import com.stiffrock.chat.model.MyAdapter;
import com.stiffrock.chat.model.RetrofitClient;
import com.stiffrock.chat.model.WebSocketClient;
import com.stiffrock.chat.utils.OnMessageReceivedListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements OnMessageReceivedListener {
    private final List<Item> messagesList = new ArrayList<>();
    private EditText etMensaje;

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    private ApiService apiService;

    private final Random randomId = new Random();

    private String recipient;

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

        recipient = getIntent().getStringExtra("recipient");

        etMensaje = findViewById(R.id.etMensaje);

        findViewById(R.id.sendText).setOnClickListener(e -> sendMessage());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyAdapter(messagesList);
        recyclerView.setAdapter(adapter);

        apiService = RetrofitClient.getApiService();

        //TODO: Revise message loading at start, this only loads recived messages but not the ones you sent.
        apiGetMessages(CurrentUser.getUsername());

        WebSocketClient.getInstance().setOnMessageReceivedListener(this);
    }

    @Override
    public void onMessageReceived(String message) {
        addTextBubble(2, message);
    }

    public void addTextBubble(int itemType, String text) {
        if (itemType == 1) {
            messagesList.add(new ItemMessageSent(text));
        } else if (itemType == 2) {
            messagesList.add(new ItemMessageRecieved(text));
        }

        adapter.notifyItemInserted(messagesList.size() - 1);
        recyclerView.scrollToPosition(messagesList.size() - 1);
    }

    private void sendMessage() {
        String texto = etMensaje.getText().toString().trim();

        if (texto.isBlank()) return;

        etMensaje.setText("");

        Long id = randomId.nextLong();
        String sender = CurrentUser.getUsername();
        LocalDateTime timestamp = LocalDateTime.now();

        Message message = new Message(id, sender, recipient, texto, timestamp);

        addTextBubble(1, texto);

        apiSendMessage(message);
    }

    //TODO: HANDLE FALIED CONNECTIONS
    private void apiSendMessage(Message message) {
        Call<Message> call = apiService.sendMessage(message);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (!response.isSuccessful())
                    Log.e(TAG, "Error en la respuesta: " + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Log.e(TAG, "Fallo en la llamada: " + t.getMessage());
            }
        });
    }

    private void apiGetMessages(String user) {
        Call<List<Message>> call = apiService.recieveMessage(user);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
                if (response.isSuccessful()) {
                    List<Message> messageList = response.body();

                    if (messageList != null) for (Message message : messageList) {
                        addTextBubble(2, message.getMensaje());
                    }
                } else {
                    Log.e(TAG, "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable t) {
                Log.e(TAG, "Fallo en la llamada: " + t.getMessage());
            }
        });
    }
}