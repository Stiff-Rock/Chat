package com.stiffrock.chat;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.stiffrock.chat.dto.CredentialsDTO;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.network.WebSocketClient;
import com.stiffrock.chat.utils.SecureStorage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LauncherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        try {
            handleAutoLogin();
        } catch (Exception e) {
            if (e.getMessage() != null) Log.e(TAG, e.getMessage());
            startActivity(AuthActivity.class);
        }
    }

    private void handleAutoLogin() {
        SharedPreferences sp = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        boolean rememberLogIn = sp.getBoolean("rememberLogIn", false);

        if (!rememberLogIn) {
            startActivity(AuthActivity.class);
            return;
        }

        CredentialsDTO credentials = new SecureStorage().getUserCredentials(this);
        if (credentials.getUsername() == null || credentials.getPassword() == null) {
            startActivity(AuthActivity.class);
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
                    startActivity(HomeActivity.class);
                    User user = response.body();
                    CurrentUser.setCurrentUser(user);
                    WebSocketClient.getInstance().connect();
                    Toast.makeText(LauncherActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(AuthActivity.class);
                    sp.edit().putBoolean("rememberLogIn", false).apply();
                    Log.e(TAG, "Error handling autologin: Server could not authenticate");
                    Toast.makeText(LauncherActivity.this, "No pudimos iniciar sesión, intenta nuevamente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                startActivity(AuthActivity.class);
                Log.e(TAG, "Error handling autologin: " + t.getMessage());
                Toast.makeText(LauncherActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void startActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        finish();
    }
}
