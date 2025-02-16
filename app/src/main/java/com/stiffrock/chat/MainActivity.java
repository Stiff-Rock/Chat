package com.stiffrock.chat;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.stiffrock.chat.fragments.ChatFragment;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.model.WebSocketClient;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Animaciones de entrada y salida entre fragments
        transaction.setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
        );

        transaction.replace(R.id.fcvMainActivity, fragment);

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fcvMainActivity);
        if (currentFragment != null)
            transaction.addToBackStack(currentFragment.getClass().getName());

        transaction.commit();
    }
}