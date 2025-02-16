package com.stiffrock.chat.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stiffrock.chat.MainActivity;
import com.stiffrock.chat.R;

public class SignUpFragment extends Fragment {
    private EditText etUsername, etPassword1, etPassword2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        etUsername = view.findViewById(R.id.etUsername);
        etPassword1 = view.findViewById(R.id.etSigInPwd1);
        etPassword2 = view.findViewById(R.id.etSigInPwd2);
        view.findViewById(R.id.btnSignIn).setOnClickListener(v -> handleSignIn());
        view.findViewById(R.id.LogInRedirect).setOnClickListener(v -> redirectToLogIn());

        return view;
    }

    private void  handleSignIn() {
        String inputUsername = etUsername.getText().toString().trim();
        String inputPassword1 = etPassword1.getText().toString().trim();
        String inputPassword2 = etPassword2.getText().toString().trim();

        redirectToLogIn();
    }

    private void redirectToLogIn() {
        ((MainActivity) requireActivity()).replaceFragment(new LogInFragment());
    }
}