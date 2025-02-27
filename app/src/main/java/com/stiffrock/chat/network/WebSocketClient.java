package com.stiffrock.chat.network;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.utils.WebSocketNotificationListener;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient {
    private static WebSocketClient instance;
    private WebSocket webSocket;
    private OkHttpClient client;
    private String APP_WEB_SOCKET_URL;
    private String GROUP_CHAT_WEB_SOCKET_URL;

    private int reconnectAppAttempts = 0;
    private int reconnectGroupAttempts = 0;
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    private static final long INITIAL_RECONNECT_DELAY_MS = 1000;

    private final Handler appHandler = new Handler(Looper.getMainLooper());
    private final Handler groupHandler = new Handler(Looper.getMainLooper());

    private boolean isConnected;

    private WebSocketNotificationListener listener;

    private WebSocketClient() {
        User user = CurrentUser.getCurrentUser();
        if (user != null) {
            String username = user.getUsername();
            String usernameQueryParameter = "?username=" + username;
            APP_WEB_SOCKET_URL = "ws://" + ServerConfig.SOCKET_ADDR + "/app" + usernameQueryParameter;
            GROUP_CHAT_WEB_SOCKET_URL = "ws://" + ServerConfig.SOCKET_ADDR + "/chat/group/{groupId}" + usernameQueryParameter;
            client = new OkHttpClient();
        } else {
            Log.e(TAG, "Error creating WebSocketClient: Current user is null");
        }
    }

    public static WebSocketClient getInstance() {
        if (instance == null) instance = new WebSocketClient();
        return instance;
    }

    //TODO: HANDLE FALIED CONNECTIONS
    public void connect() {
        Request request = new Request.Builder().url(APP_WEB_SOCKET_URL).build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                super.onOpen(webSocket, response);

                cancelReconnectionAttempts();
                isConnected = true;
                Log.d(TAG, "Connected to AppWebSocket");
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                super.onMessage(webSocket, text);

                if (listener != null) {
                    appHandler.post(() -> listener.onNotificationReceived(text));
                }
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                super.onFailure(webSocket, t, response);

                isConnected = false;
                Log.d(TAG, "WebSocket Error: " + t.getMessage());
                handleAppWsRecconection();
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosed(webSocket, code, reason);

                isConnected = false;
            }
        });
    }

    private void handleAppWsRecconection() {
        if (reconnectAppAttempts < MAX_RECONNECT_ATTEMPTS) {
            long delay = INITIAL_RECONNECT_DELAY_MS * (long) Math.pow(2, reconnectAppAttempts);

            appHandler.postDelayed(() -> {
                if (!isConnected) {
                    Log.w(TAG, "Attempting reconnect #" + (reconnectAppAttempts + 1));
                    connect();
                    reconnectAppAttempts++;
                }
            }, delay);
        } else {
            Log.e(TAG, "Max reconnect attempts reached");
            reconnectAppAttempts = 0;
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Normal disconnection");
            isConnected = false;
        }
    }

    public void connectToGroupChat() {
        Long groupId = CurrentUser.getCurrentChat().getId();
        String url = GROUP_CHAT_WEB_SOCKET_URL.replace("{groupId}", String.valueOf(groupId));
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                super.onOpen(webSocket, response);

                cancelReconnectionAttempts();
                Log.d(TAG, "Connected to GroupWebSocket");
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                if (listener != null) {
                    groupHandler.post(() -> listener.onNotificationReceived(text));
                }
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                String message = t.getMessage();
                if (message == null) message = "Abrupt disconnection";

                Log.d(TAG, "GorupChatWebSocket Error: " + message);

                handleGroupWsRecconection();
            }
        });
    }

    private void handleGroupWsRecconection() {
        if (reconnectGroupAttempts < MAX_RECONNECT_ATTEMPTS) {
            long delay = INITIAL_RECONNECT_DELAY_MS * (long) Math.pow(2, reconnectGroupAttempts);

            groupHandler.postDelayed(() -> {
                if (!isConnected) {
                    Log.w(TAG, "Attempting reconnect #" + (reconnectGroupAttempts + 1));
                    connectToGroupChat();
                    reconnectGroupAttempts++;
                }
            }, delay);
        } else {
            Log.e(TAG, "Max reconnect attempts reached");
            reconnectGroupAttempts = 0;
        }
    }

    public void disconnectFromGroupChat() {
        if (webSocket != null) {
            webSocket.close(1000, "Cierre normal");
        }
    }

    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        } else {
            Log.e(TAG, "WebSocket no está conectado.");
        }
    }

    private void cancelReconnectionAttempts() {
        appHandler.removeCallbacksAndMessages(null);
        groupHandler.removeCallbacksAndMessages(null);
        reconnectAppAttempts = 0;
        reconnectGroupAttempts = 0;
    }

    public void setOnNotificationReceivedListener(WebSocketNotificationListener listener) {
        this.listener = listener;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
