package com.stiffrock.chat;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.stiffrock.chat.dto.CredentialsDTO;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.network.WebSocketClient;
import com.stiffrock.chat.utils.FragmentContainerActivity;
import com.stiffrock.chat.utils.SecureStorage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends FragmentContainerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        handleAutoLogin();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void handleAutoLogin() {
        SharedPreferences sp = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        boolean rememberLogIn = sp.getBoolean("rememberLogIn", false);

        if (!rememberLogIn) return;

        SecureStorage ss = new SecureStorage();
        CredentialsDTO credentials = ss.getUserCredentials(this);

        if (credentials.getUsername() == null || credentials.getPassword() == null) {
            sp.edit().putBoolean("rememberLogIn", false).apply();
            Toast.makeText(this, "Error, por favor ingresa tus datos", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error handling autologin: stored credentials are null");
            return;
        }

        Call<User> call = RetrofitClient.getApiService().logInUser(credentials);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    navigateToHomeActivity();
                    User user = response.body();
                    CurrentUser.setCurrentUser(user);
                    WebSocketClient.getInstance().connect();
                    Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                } else {
                    sp.edit().putBoolean("rememberLogIn", false).apply();
                    Log.e(TAG, "Error handling autologin: Server could not authenticate");
                    Toast.makeText(MainActivity.this, "No pudimos iniciar sesión, intenta nuevamente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG, "Error handling autologin: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void navigateToHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}