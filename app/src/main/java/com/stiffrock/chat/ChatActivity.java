package com.stiffrock.chat;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stiffrock.chat.adapters.MyAdapter;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemMessageRecieved;
import com.stiffrock.chat.items.ItemMessageSent;
import com.stiffrock.chat.model.Chat;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.Message;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.network.WebSocketClient;
import com.stiffrock.chat.utils.OnMessageReceivedListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements OnMessageReceivedListener {
    private final List<Item> messagesList = new ArrayList<>();
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

        etMensaje = findViewById(R.id.etMensaje);

        findViewById(R.id.sendText).setOnClickListener(e -> sendMessage());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyAdapter(messagesList);
        recyclerView.setAdapter(adapter);

        apiService = RetrofitClient.getApiService();

        //TODO: LOAD MESSAGES AT THE START

        WebSocketClient.getInstance().setOnMessageReceivedListener(this);
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

        User sender = CurrentUser.getCurrentUser();
        LocalDateTime timestamp = LocalDateTime.now();
        Chat chat = CurrentUser.getCurrentChat();

        Message message = new Message(sender, chat, chat.getParticipants(), texto, timestamp);

        addTextBubble(1, texto);

        apiSendMessage(message);
    }

    //TODO: HANDLE FALIED CONNECTIONS
    private void apiSendMessage(Message message) {
        Call<Message> call = apiService.sendMessage(message);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Error en la respuesta: " + response.code());
                    Toast.makeText(getBaseContext(), "Error enviado el mensaje", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Log.e(TAG, "Fallo en la llamada: " + t.getMessage());
                Toast.makeText(getBaseContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void apiGetMessage(Long messageId) {
        Call<Message> call = apiService.recieveMessage(messageId);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    Message message = response.body();

                    if (message == null) return;

                    addTextBubble(2, message.getMessageContent());
                } else {
                    Log.e(TAG, "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Log.e(TAG, "Fallo en la llamada: " + t.getMessage());
            }
        });
    }

    //TODO: QUIZAS ESTO TAMBIEN EN EL CONTANCTFRAGMENT CON LA NOTIFICACION DE TOAST
    @Override
    public void onMessageReceived(String messageId) {
        apiGetMessage(Long.valueOf(messageId));
    }
}