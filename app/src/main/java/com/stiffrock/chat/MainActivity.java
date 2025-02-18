package com.stiffrock.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.stiffrock.chat.fragments.main.LogInFragment;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.network.WebSocketClient;
import com.stiffrock.chat.utils.FragmentContainerActivity;


public class MainActivity extends FragmentContainerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleAutoLogin();
    }

    private void handleAutoLogin() {
        SharedPreferences sp = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        boolean rememberLogIn = sp.getBoolean("rememberLogIn", false);

        if (rememberLogIn) {
            String rememberedUser = sp.getString("storedUser", null);

            if (rememberedUser == null) {
                Toast.makeText(this, "Error, por favor inica sesión manualmente", Toast.LENGTH_SHORT).show();
                return;
            }

            CurrentUser.setUsername(rememberedUser);
            WebSocketClient.getInstance().connect();
            navigateToHomeActivity();
        } else {
            replaceFragment(new LogInFragment());
        }
    }

    public void navigateToHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}