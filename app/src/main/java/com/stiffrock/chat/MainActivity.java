package com.stiffrock.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.stiffrock.chat.fragments.HomeScreenFragment;
import com.stiffrock.chat.fragments.LogInFragment;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.WebSocketClient;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            replaceFragment(new HomeScreenFragment());
        } else {
            replaceFragment(new LogInFragment());
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Animaciones de entrada y salida entre fragments
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        transaction.replace(R.id.fcvMainActivity, fragment);

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fcvMainActivity);
        if (currentFragment != null)
            transaction.addToBackStack(currentFragment.getClass().getName());

        transaction.commit();
    }
}