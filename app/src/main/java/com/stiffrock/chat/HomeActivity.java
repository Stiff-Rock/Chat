package com.stiffrock.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.stiffrock.chat.fragments.home.AddContactFragment;
import com.stiffrock.chat.fragments.home.AddGroupFragment;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.network.WebSocketClient;
import com.stiffrock.chat.utils.FragmentContainerActivity;

public class HomeActivity extends FragmentContainerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addContact) {
            replaceFragment(new AddContactFragment());
            return true;
        } else if (item.getItemId() == R.id.addGroup) {
            replaceFragment(new AddGroupFragment());
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
        navigateToActivity(MainActivity.class, null);
    }

    public void navigateToActivity(Class<?> targetActivity, Bundle bundle) {
        Intent intent = new Intent(HomeActivity.this, targetActivity);
        if (bundle != null) intent.putExtras(bundle);
        startActivity(intent);
    }
}