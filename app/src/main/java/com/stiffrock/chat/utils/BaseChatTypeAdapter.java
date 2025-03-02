package com.stiffrock.chat.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.stiffrock.chat.model.BaseChat;
import com.stiffrock.chat.model.GroupChat;
import com.stiffrock.chat.model.PrivateChat;

import java.lang.reflect.Type;

/**
 * Clase que ayuda a Gson a saber como serializar la clase {@link BaseChat} y como gestonar la herencia
 */
public class BaseChatTypeAdapter implements JsonSerializer<BaseChat>, JsonDeserializer<BaseChat> {
    private static final String TYPE = "type";

    @Override
    public JsonElement serialize(BaseChat src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = context.serialize(src, src.getClass()).getAsJsonObject();
        jsonObject.addProperty(TYPE, src.getClass().getSimpleName());
        return jsonObject;
    }

    @Override
    public BaseChat deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (!jsonObject.has(TYPE)) {
            throw new JsonParseException("Missing 'type' field in JSON");
        }

        String type = jsonObject.get(TYPE).getAsString();

        switch (type) {
            case "GroupChat":
                return context.deserialize(json, GroupChat.class);
            case "PrivateChat":
                return context.deserialize(json, PrivateChat.class);
            default:
                throw new JsonParseException("Unknown type: " + type);
        }
    }
}
