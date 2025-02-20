package com.stiffrock.chat.utils;

import com.google.gson.*;
import com.stiffrock.chat.model.BaseChat;
import com.stiffrock.chat.model.GroupChat;
import com.stiffrock.chat.model.PrivateChat;

import java.lang.reflect.Type;

public class BaseChatTypeAdapter implements JsonSerializer<BaseChat>, JsonDeserializer<BaseChat> {
    private static final String TYPE = "type";

    @Override
    public JsonElement serialize(BaseChat src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TYPE, src.getClass().getSimpleName());
        jsonObject.add("data", context.serialize(src, src.getClass()));
        return jsonObject;
    }

    @Override
    public BaseChat deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get(TYPE).getAsString();
        JsonElement data = jsonObject.get("data");

        switch (type) {
            case "GroupChat":
                return context.deserialize(data, GroupChat.class);
            case "PrivateChat":
                return context.deserialize(data, PrivateChat.class);
            default:
                throw new JsonParseException("Unknown type: " + type);
        }
    }
}