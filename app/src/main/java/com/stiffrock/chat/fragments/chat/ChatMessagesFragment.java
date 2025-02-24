package com.stiffrock.chat.fragments.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.stiffrock.chat.ChatActivity;
import com.stiffrock.chat.R;


public class ChatMessagesFragment extends Fragment {
    public RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_messages, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        EditText etMensaje = view.findViewById(R.id.etMensaje);
        view.findViewById(R.id.sendText).setOnClickListener(e -> ((ChatActivity) requireActivity()).sendMessage(etMensaje));
        return view;
    }
}