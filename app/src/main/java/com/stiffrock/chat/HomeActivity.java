package com.stiffrock.chat;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.stiffrock.chat.dto.PrivateChatDTO;
import com.stiffrock.chat.fragments.home.AddGroupFragment;
import com.stiffrock.chat.fragments.home.ContactsFragment;
import com.stiffrock.chat.fragments.home.OnlineUsersFragment;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.PrivateChat;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.network.WebSocketClient;
import com.stiffrock.chat.utils.FragmentContainerActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends FragmentContainerActivity {
    private ApiService apiService;
    public Toolbar toolbar;

    private WebSocketClient wsClient;

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

        wsClient = WebSocketClient.getInstance();

        apiService = RetrofitClient.getApiService();

        toolbar = findViewById(R.id.toolbar);

        if (!wsClient.isConnected()) wsClient.connect();

        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!wsClient.isConnected()) wsClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && wsClient.isConnected()) wsClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addContact) {
            addContactDialog();
            return true;
        } else if (item.getItemId() == R.id.addGroup) {
            toggleHomeButton(true);
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fcv);
            if (currentFragment instanceof ContactsFragment) {
                ContactsFragment cf = (ContactsFragment) currentFragment;
                replaceFragment(new AddGroupFragment(new ArrayList<>(cf.userChatMap.keySet())));
            }
            return true;
        } else if (item.getItemId() == R.id.showOnlineUsers) {
            toggleHomeButton(true);
            replaceFragment(new OnlineUsersFragment());
            return true;
        } else if (item.getItemId() == R.id.logOut) {
            logOut();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            toggleHomeButton(false);
            replaceFragment(new ContactsFragment());
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void toggleHomeButton(boolean active) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(active);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow);
        }
    }

    //TODO: MAKE BETTER
    private void addContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = 30;
        layout.setPadding(padding, padding, padding, padding);

        EditText editText = new EditText(this);
        editText.setHint("Nombre de usuario");
        layout.addView(editText);

        int textColor = getColor(R.color.standard_text_color_tertiary);

        SpannableString title = new SpannableString("Nuevo contacto");
        title.setSpan(new ForegroundColorSpan(textColor), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setTitle(title).setView(layout).setPositiveButton("Aceptar", (dialog, which) -> {
            String userInput = editText.getText().toString().trim();
            if (userInput.isBlank()) return;
            getUserByUsername(userInput);
        }).setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        int backgroundColor = getColor(R.color.md_theme_dark_primaryContainer);

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (positiveButton != null) positiveButton.setTextColor(textColor);


        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (negativeButton != null) negativeButton.setTextColor(textColor);


        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
    }

    private void getUserByUsername(String name) {
        Call<User> call = apiService.getUserByUsername(name);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    addContact(user);
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

    public void addContact(User user) {
        Long userId1 = CurrentUser.getCurrentUser().getId();
        Long userId2 = user.getId();

        PrivateChatDTO ccd = new PrivateChatDTO(userId1, userId2);

        Call<PrivateChat> call = apiService.addContact(ccd);
        call.enqueue(new Callback<PrivateChat>() {
            @Override
            public void onResponse(@NonNull Call<PrivateChat> call, @NonNull Response<PrivateChat> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PrivateChat chat = response.body();
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fcv);
                    if (currentFragment instanceof ContactsFragment) {
                        ContactsFragment cf = (ContactsFragment) currentFragment;
                        User recipient = chat.getContact(CurrentUser.getCurrentUser());
                        cf.addContact(chat, recipient);
                    } else {
                        toggleHomeButton(false);
                        replaceFragment(new ContactsFragment());
                    }
                    Toast.makeText(HomeActivity.this, "Contacto añadido", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, "Error añadiendo contacto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PrivateChat> call, @NonNull Throwable throwable) {
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
        navigateToActivity(AuthActivity.class);
    }

    public void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(HomeActivity.this, targetActivity);
        startActivity(intent);
    }
}