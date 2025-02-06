package com.stiffrock.chat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stiffrock.chat.model.User;


public class MainActivity extends AppCompatActivity {
    private EditText etUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnEntrar = findViewById(R.id.btnEntrar);
        etUser = findViewById(R.id.etUser);

        btnEntrar.setOnClickListener(v -> logIn());
    }

    private void logIn() {
        String usuario = etUser.getText().toString().trim();

        if (usuario.isBlank()) {
            Toast.makeText(this, "Debes introducir un usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        User.setUsername(usuario);

        openChatActivity();
    }

    private void openChatActivity() {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        startActivity(intent);
    }
}