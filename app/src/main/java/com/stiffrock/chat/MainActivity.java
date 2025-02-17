package com.stiffrock.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.stiffrock.chat.fragments.HomeScreenFragment;
import com.stiffrock.chat.fragments.LogInFragment;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.WebSocketClient;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addChat) {
            getSupportFragmentManager().getFragment()
            return true;
        } else if (item.getItemId() == R.id.logOut) {
            logOut();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void logOut() {
        CurrentUser.setUsername("");
        WebSocketClient.getInstance().disconnect();

        SharedPreferences sp = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        sp.edit().putBoolean("rememberLogIn", false).apply();
        sp.edit().putString("storedUser", "").apply();

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        replaceFragment(new LogInFragment());
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

        if (fragment instanceof HomeScreenFragment) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }

        transaction.commit();
    }
}