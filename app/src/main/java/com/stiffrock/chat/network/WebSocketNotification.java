package com.stiffrock.chat.network;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stiffrock.chat.model.BaseChat;
import com.stiffrock.chat.model.Message;
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

            Object content = null;

            // Manejar el parseo del contenido
            switch (action) {
                case ADD_CONTACT:
                case DELETE_CONTACT:
                    content = GsonManager.gson.fromJson(json.get("content").getAsString(), BaseChat.class);
                    break;

                case MESSAGE_RECEIVED:
                    content = GsonManager.gson.fromJson(json.get("content").getAsString(), Message.class);
                    break;

                case MESSAGE_READ:
                    // Handle MESSAGE_READ logic here
                    break;

                case MESSAGE_DELETED:
                    // Handle MESSAGE_DELETED logic here
                    break;

                case USER_DISCONNECTED_FROM_GROUP:
                    // Handle USER_DISCONNECTED_FROM_GROUP logic here
                    break;

                case USER_DISCONNECTED_FROM_APP:
                    // Handle USER_DISCONNECTED_FROM_APP logic here
                    break;

                case USER_LEFT_GROUP:
                    // Handle USER_LEFT_GROUP logic here
                    break;

                case USER_JOINED_GROUP:
                    // Handle USER_JOINED_GROUP logic here
                    break;

                case ERROR:
                    break;

                default:
                    throw new Exception("Invalid WebSocketAction value");
            }

            return new WebSocketNotification(action, content);
        } catch (Exception e) {
            System.err.println("Error parsing WebSocket notification: " + e.getMessage());
            return new WebSocketNotification(WebSocketAction.ERROR, null);
        }
    }
}
