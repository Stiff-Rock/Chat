package com.stiffrock.chat.network;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stiffrock.chat.model.BaseChat;
import com.stiffrock.chat.model.Message;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.model.WebSocketAction;
import com.stiffrock.chat.utils.GsonManager;

public class WebSocketNotification {
    private final WebSocketAction action;
    private final Object content;

    private WebSocketNotification(WebSocketAction action, Object content) {
        this.action = action;
        this.content = content;
    }

    @NonNull
    public WebSocketAction getAction() {
        return action;
    }

    public Object getContent() {
        return content;
    }

    public static WebSocketNotification parse(String notification) {
        try {
            JsonObject json = JsonParser.parseString(notification).getAsJsonObject();
            WebSocketAction action = WebSocketAction.valueOf(json.get("action").getAsString());

            Object content;

            // Manejar el parseo del contenido
            switch (action) {
                case ADD_CHAT:
                    content = GsonManager.gson.fromJson(json.get("content").getAsString(), BaseChat.class);
                    break;

                case MESSAGE_RECEIVED:
                case USER_CONNECTED_TO_GROUP_CHAT:
                case USER_DISCONNECTED_FROM_GROUP_CHAT:
                case MESSAGE_DELETED:
                case MESSAGE_READ:
                    content = GsonManager.gson.fromJson(json.get("content").getAsString(), Message.class);
                    break;

                case GROUP_CHAT_CHANGED:
                case GROUP_CHAT_DELETION:
                    content = json.getAsJsonObject("content");
                    break;

                case DELETE_CONTACT:
                case USER_CONNECTED:
                case USER_DISCONNECTED:
                    content = GsonManager.gson.fromJson(json.get("content").getAsString(), User.class);
                    break;
                default:
                    throw new Exception("Invalid WebSocketAction value");
            }

            return new WebSocketNotification(action, content);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing WebSocket notification: " + e.getMessage());
            return new WebSocketNotification(WebSocketAction.ERROR, null);
        }
    }
}
