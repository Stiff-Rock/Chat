package com.stiffrock.chat.network;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.utils.OnMessageReceivedListener;
import com.stiffrock.chat.utils.ServerConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient {
    private static WebSocketClient instance;
    private WebSocket webSocket;
    private final OkHttpClient client;
    private final String WEB_SOCKET_URL;

    private OnMessageReceivedListener listener;

    private WebSocketClient() {
        client = new OkHttpClient();
        String usernameQueryParameter = "?username=" + CurrentUser.getCurrentUser();
        WEB_SOCKET_URL = "ws://" + ServerConfig.SOCKET_ADDR + "/chat" + usernameQueryParameter;
    }

    public static WebSocketClient getInstance() {
        if (instance == null) {
            instance = new WebSocketClient();
        }
        return instance;
    }

    public void setOnMessageReceivedListener(OnMessageReceivedListener listener) {
        this.listener = listener;
    }

    //TODO: HANDLE FALIED CONNECTIONS
    public void connect() {
        Request request = new Request.Builder().url(WEB_SOCKET_URL).build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                if (listener != null) {
                    new Handler(Looper.getMainLooper()).post(() -> listener.onMessageReceived(text));
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                Log.d(TAG, "Mensaje binario recibido: " + bytes.hex());
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                Log.d(TAG, "Error en WebSocket: " + t.getMessage());
            }
        });
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Cierre normal");
        }
    }
}
