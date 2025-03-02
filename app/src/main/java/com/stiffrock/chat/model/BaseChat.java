package com.stiffrock.chat.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Clase abstracta que representa un chat, tanto privado como grupal, y contiene atributos comunes
 * a ambos.
 * </p>
 * Atributos:
 * <p>
 * - {@link #id}: Id único del chat en la BBDD.
 * <p>
 * - {@link #participants}: Set de participantes de este chat.
 * <p>
 * - {@link #messages}: Lista de mensajes que han sido enviados en este chat.
 */
public abstract class BaseChat {
    private Long id;
    private Set<User> participants = new HashSet<>();
    private List<Message> messages = new ArrayList<>();

    public BaseChat() {
    }

    // Getters y setters comunes
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseChat chat = (BaseChat) o;
        return Objects.equals(id, chat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
