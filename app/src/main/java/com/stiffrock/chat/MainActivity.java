package com.stiffrock.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.stiffrock.chat.fragments.main.LogInFragment;
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

        //TODO: ESTO
        if (rememberLogIn) {
//            String userName = sp.getString("storedUserName", null);
//            long userId = sp.getLong("storedUserId", -1);
//
//            if (userName == null || userId == 0) {
//                sp.edit().putBoolean("rememberLogIn", false).apply();
//                Toast.makeText(this, "Error, por favor inica sesión manualmente", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            CurrentUser.setCurrentUser(new User(userId, userName));
//            WebSocketClient.getInstance().connect();
//            navigateToHomeActivity();
        } else {
            replaceFragment(new LogInFragment());
        }
    }

    public void navigateToHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}