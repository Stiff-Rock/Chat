package com.stiffrock.chat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.stiffrock.chat.MainActivity;
import com.stiffrock.chat.R;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.WebSocketClient;

public class LogInFragment extends Fragment {
    private EditText etUsername, etPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPwd);
        view.findViewById(R.id.btnLogin).setOnClickListener(v -> handleLogin());
        view.findViewById(R.id.sigInRedirect).setOnClickListener(v -> redirectToSignUp());

        return view;
    }

    //TODO: HANDLE "REMEMBER ME" IN SHARED PREFS AND ADD LOG OUT
    private void handleLogin() {
        String inputUsername = etUsername.getText().toString().trim();
        String inputPassword = etPassword.getText().toString().trim();

        if (inputUsername.isBlank())
            return;

        CurrentUser.setUsername(inputUsername);
        WebSocketClient.getInstance().connect();

        ((MainActivity) requireActivity()).replaceFragment(new HomeScreenFragment());
    }

    private void redirectToSignUp() {
        ((MainActivity) requireActivity()).replaceFragment(new SignUpFragment());
    }
}