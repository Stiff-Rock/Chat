package com.stiffrock.chat;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.stiffrock.chat.dto.CreateChatDTO;
import com.stiffrock.chat.fragments.home.AddGroupFragment;
import com.stiffrock.chat.fragments.home.ContactsFragment;
import com.stiffrock.chat.model.Chat;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.network.WebSocketClient;
import com.stiffrock.chat.utils.FragmentContainerActivity;

import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends FragmentContainerActivity {
    private ApiService apiService;

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

        apiService = RetrofitClient.getApiService();

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
            addContactDialog();
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

    private void addContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = 30;
        layout.setPadding(padding, padding, padding, padding);

        EditText editText = new EditText(this);
        editText.setHint("Nombre de usuario");
        layout.addView(editText);

        builder.setTitle("Nuevo contacto").setView(layout).setPositiveButton("OK", (dialog, which) -> {
            String userInput = editText.getText().toString().trim();
            if (userInput.isBlank()) return;
            getUserByUsername(userInput);
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create().show();
    }

    private void getUserByUsername(String name) {
        Call<User> call = apiService.getUserByUsername(name);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "YES");
                    addContact(response.body());
                } else {
                    Toast.makeText(HomeActivity.this, "El usuario introducido no existe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG, "GetUserByUsername request failed: " + t.getMessage());
                Toast.makeText(HomeActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //TODO: AQUI PASA ALGO RARO DE COJONES QUE SE REINICIA
    private void addContact(User user) {
        Set<User> participants = new HashSet<>();
        participants.add(CurrentUser.getCurrentUser());
        participants.add(user);

        CreateChatDTO ccd = new CreateChatDTO(user.getUsername(), false, participants);

        Call<Chat> call = apiService.addContact(ccd);
        call.enqueue(new Callback<Chat>() {
            @Override
            public void onResponse(@NonNull Call<Chat> call, @NonNull Response<Chat> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fcv);
                    if (currentFragment != null)
                        ((ContactsFragment) currentFragment).addContact(response.body());
                    Toast.makeText(HomeActivity.this, "Contacto añadido", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, "Error añadiendo contacto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Chat> call, @NonNull Throwable throwable) {
                Log.e(TAG, "GetUserByUsername request failed: " + throwable.getMessage());
                Toast.makeText(HomeActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logOut() {
        CurrentUser.setCurrentUser(null);
        WebSocketClient.getInstance().disconnect();

        SharedPreferences sp = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        sp.edit().putBoolean("rememberLogIn", false).apply();
        sp.edit().putString("storedUser", "").apply();

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        navigateToActivity(MainActivity.class);
    }

    public void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(HomeActivity.this, targetActivity);
        startActivity(intent);
    }
}