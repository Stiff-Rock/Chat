package com.stiffrock.chat;

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
import com.stiffrock.chat.model.Mensaje;
import com.stiffrock.chat.model.MyAdapter;
import com.stiffrock.chat.model.RetrofitClient;
import com.stiffrock.chat.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "RetrofitActivity";

    private final List<Item> messagesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private EditText etMensaje;

    private ApiService apiService;

    private final Random randomId = new Random();

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
        apiGetMessages(User.getUsername());
    }

    private void addTextBubble(int itemType, String text) {
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

        Long id = randomId.nextLong();
        String remitente = User.getUsername();
        String destinatario = "default";

        LocalDateTime timestamp = LocalDateTime.now();

        Mensaje mensaje = new Mensaje(id, remitente, destinatario, texto, timestamp);

        addTextBubble(1, texto);

        apiSendMessage(mensaje);
    }

    private void apiSendMessage(Mensaje mensaje) {
        Call<Mensaje> call = apiService.enviarMensaje(mensaje);
        call.enqueue(new Callback<Mensaje>() {
            @Override
            public void onResponse(@NonNull Call<Mensaje> call, @NonNull Response<Mensaje> response) {
                if (response.isSuccessful()) {
                    Mensaje mensajeEnviado = response.body();
                    Log.d(TAG, "Mensaje enviado: " + mensajeEnviado.getMensaje());
                } else {
                    Log.e(TAG, "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Mensaje> call, @NonNull Throwable t) {
                Log.e(TAG, "Fallo en la llamada: " + t.getMessage());
            }
        });
    }

    private void apiGetMessages(String usuario) {
        Call<List<Mensaje>> call = apiService.obtenerMensajes(usuario);
        call.enqueue(new Callback<List<Mensaje>>() {
            @Override
            public void onResponse(@NonNull Call<List<Mensaje>> call, @NonNull Response<List<Mensaje>> response) {
                if (response.isSuccessful()) {
                    List<Mensaje> messageList = response.body();
                    for (Mensaje mensaje : messageList) {
                        addTextBubble(2, mensaje.getMensaje());
                    }
                } else {
                    Log.e(TAG, "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Mensaje>> call, @NonNull Throwable t) {
                Log.e(TAG, "Fallo en la llamada: " + t.getMessage());
            }
        });
    }
}