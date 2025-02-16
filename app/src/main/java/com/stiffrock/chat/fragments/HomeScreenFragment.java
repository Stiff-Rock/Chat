package com.stiffrock.chat.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.stiffrock.chat.MainActivity;
import com.stiffrock.chat.R;
import com.stiffrock.chat.model.WebSocketClient;

public class HomeScreenFragment extends Fragment {
    private EditText etUserToText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        view.findViewById(R.id.btnOpenChat).setOnClickListener(e -> openChat());
        etUserToText = view.findViewById(R.id.etUserToText);

        return view;
    }

    private void openChat() {
        String recipient = etUserToText.getText().toString().trim();

        if (recipient.isBlank()) return;

        ((MainActivity) requireActivity()).replaceFragment(new ChatFragment(recipient));
    }
}