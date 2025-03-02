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
import com.stiffrock.chat.fragments.home.UserProfileFragment;
import com.stiffrock.chat.utils.CurrentUser;
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

/**
 * Clase de la activity principal de la app. Contiene los fragments para visualizar los contactos,
 * los usuarios en línea, crear un grupo y modficar tu perfil.
 * <p>
 * Hereda de {@link FragmentContainerActivity}, clase que contiene comportamientos comunes entre
 * activities que contienen fragments.
 */
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
        // En caso de haberse desconectado del websocket de la app, se reconecta
        if (!wsClient.isConnected()) wsClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // En caos de que se esté cerrando la app, se desconecta del websocket
        if (isFinishing() && wsClient.isConnected()) wsClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.userProfile) {
            // Muestra el perfil del usuario usando la app
            toggleHomeButton(true);
            replaceFragment(new UserProfileFragment());
            return true;
        } else if (item.getItemId() == R.id.addContact) {
            // Muestra un popup para introducir un nombre de usuario para añadirle como contacto
            addContactDialog();
            return true;
        } else if (item.getItemId() == R.id.addGroup) {
            // Muestra el fragment para crear un nuevo grupo
            toggleHomeButton(true);
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fcv);
            if (currentFragment instanceof ContactsFragment) {
                ContactsFragment cf = (ContactsFragment) currentFragment;
                replaceFragment(new AddGroupFragment(new ArrayList<>(cf.privateChatsMap.keySet())));
            }
            return true;
        } else if (item.getItemId() == R.id.showOnlineUsers) {
            // Muestra el fragment para ver a todos los usuarios online en la aplicación
            toggleHomeButton(true);
            replaceFragment(new OnlineUsersFragment());
            return true;
        } else if (item.getItemId() == R.id.logOut) {
            // Cierrra la sesión
            logOut();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            // Inicia el fragment de contactos
            toggleHomeButton(false);
            replaceFragment(new ContactsFragment());
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Activa o desactiva el botón que te devuelve al fragment de contactos en caso de encontrarse
     * en otro fragment.
     *
     * @param active Boolean que indica si debe activar (true) o desactivar (false)
     */
    private void toggleHomeButton(boolean active) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(active);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow);
        }
    }

    /**
     * Muestra un popup para añadir un contacto por su nombre de usuario. Al introducir
     * el nombre de usuario y presionar "Aceptar", se hace una petición al servidor para comprobar
     * si el usuario introducido existe y en caso de que si, se añade como contacto.
     */
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
            apiGetUserByUsername(userInput);
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

    /**
     * Método para enviar y gestionar una petición al servidor para comprobar si un usuario
     * existe a partir de su nombre y en caso positivo. Añadirlo como contacto con otra petición.
     *
     * @param name Nombre de usuario a enviar al servidor
     */
    private void apiGetUserByUsername(String name) {
        Call<User> call = apiService.getUserByUsername(name);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    apiAddContact(user);
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

    /**
     * Método para enviar una petición al servidor para añadir a un usuario como contacto.
     *
     * @param user Usuario a añadir como contacto
     */
    public void apiAddContact(User user) {
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

    /**
     * Cierra la sesión, desconectandose del websocket, borrando las shared preferences con las
     * credenciales del usuario previamente logeado y volviento al AuthActivity.
     */
    private void logOut() {
        wsClient.disconnect();
        CurrentUser.setCurrentUser(null);

        SharedPreferences sp = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        sp.edit().putBoolean("rememberLogIn", false).apply();
        sp.edit().putString("storedUser", "").apply();

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        navigateToActivity(AuthActivity.class);
    }

    /**
     * Navega a la activity dada por parámetro.
     *
     * @param targetActivity Clase de la activity que se va a lanzar.
     */
    public void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(HomeActivity.this, targetActivity);
        startActivity(intent);
    }
}