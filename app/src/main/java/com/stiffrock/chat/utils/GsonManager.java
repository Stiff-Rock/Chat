package com.stiffrock.chat.utils;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stiffrock.chat.model.BaseChat;

public class GsonManager {
    public static Gson gson = Converters.registerLocalDateTime(new GsonBuilder()).registerTypeAdapter(BaseChat.class, new BaseChatTypeAdapter()).create();
}
