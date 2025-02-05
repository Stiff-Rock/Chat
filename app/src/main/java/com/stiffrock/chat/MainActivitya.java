package com.stiffrock.chat;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.stiffrock.chat.model.ApiService;
import com.stiffrock.chat.model.Mensaje;
import com.stiffrock.chat.model.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivitya extends AppCompatActivity {

    private static final String TAG = "RetrofitActivity";
    private ApiService apiService;
    private Button btnEnviar, btnRecibir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = RetrofitClient.getApiService();

        btnEnviar = findViewById(R.id.btnEnviar);
        btnRecibir = findViewById(R.id.btnRecibir);

        btnEnviar.setOnClickListener(v -> enviarMensaje(new Mensaje(null, "remitente", "destinatario", "Hola, ¿cómo estás?", null)));

        btnRecibir.setOnClickListener(v -> obtenerMensajes("destinatario"));
    }

    private void enviarMensaje(Mensaje mensaje) {
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

    private void obtenerMensajes(String usuario) {
        Call<List<Mensaje>> call = apiService.obtenerMensajes(usuario);
        call.enqueue(new Callback<List<Mensaje>>() {
            @Override
            public void onResponse(@NonNull Call<List<Mensaje>> call, @NonNull Response<List<Mensaje>> response) {
                if (response.isSuccessful()) {
                    List<Mensaje> listaMensajes = response.body();
                    for (Mensaje mensaje : listaMensajes) {
                        Log.d(TAG, "Mensaje recibido: " + mensaje.getMensaje());
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