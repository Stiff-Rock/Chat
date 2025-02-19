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
import com.stiffrock.chat.dto.MessageDTO;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemMessageRecieved;
import com.stiffrock.chat.items.ItemMessageSent;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.Message;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.network.WebSocketClient;
import com.stiffrock.chat.utils.WebSocketNotificationListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements WebSocketNotificationListener {
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
        WebSocketClient.getInstance().setOnNotificationReceivedListener(this);
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
        String text = etMensaje.getText().toString().trim();
        if (text.isBlank()) return;
        etMensaje.setText("");
        addTextBubble(1, text);
        Long userId = CurrentUser.getCurrentUser().getId();
        Long chatID = CurrentUser.getCurrentChat().getChatId();
        apiSendMessage(new MessageDTO(userId, chatID, text));
    }

    //TODO: HANDLE FALIED CONNECTIONS
    private void apiSendMessage(MessageDTO messageDTO) {
        Call<Message> call = apiService.sendMessage(messageDTO);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Message sent: " + response.body());
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

    private void apiGetMessage(Long messageId) {
        Call<Message> call = apiService.recieveMessage(messageId);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Message message = response.body();
                    addTextBubble(2, message.getMessageContent());
                } else {
                    Log.e(TAG, "Error recieving message: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Log.e(TAG, "RecieveMessage request failed: " + t.getMessage());
            }
        });
    }

    //TODO: QUIZAS ESTO TAMBIEN EN EL CONTANCTFRAGMENT CON LA NOTIFICACION DE TOAST
    @Override
    public void onNotificationReceived(String notification) {
        Log.d(TAG, "Message recieved: " + notification);
        apiGetMessage(Long.valueOf(notification));
    }
}