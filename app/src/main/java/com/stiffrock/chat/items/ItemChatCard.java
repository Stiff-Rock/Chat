package com.stiffrock.chat.items;

import java.util.Collections;
import java.util.List;

public class ItemChatCard extends Item {
    private String chatName;
    private List<String> participants;
    private final boolean isGroupChat;

    private String chatId;

    public ItemChatCard(String chatName, List<String> participants, boolean isGroupChat) {
        this.chatName = chatName;
        this.participants = participants;
        this.isGroupChat = isGroupChat;

        generateChatId();
    }

    public ItemChatCard(String chatName, List<String> participants, boolean isGroupChat, String chatID) {
        this.chatName = chatName;
        this.participants = participants;
        this.isGroupChat = isGroupChat;
        this.chatId = chatID;
    }

    // Getters
    public String getChatName() {
        return chatName;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public String getChatId() {
        return chatId;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    //Setters
    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    @Override
    public int getType() {
        return 2;
    }

    //TODO: Make local Sqlite database

    //Genera un identificador único para el chat
    public void generateChatId() {
        Collections.sort(participants);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < participants.size(); i++) {
            sb.append(participants.get(i));
            if (i < participants.size() - 1) {
                sb.append("_");
            }
        }

        sb.append("_").append(System.currentTimeMillis());

        chatId = sb.toString();
    }
}
