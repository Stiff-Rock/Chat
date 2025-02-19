package com.stiffrock.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.stiffrock.chat.dto.LoginDTO;
import com.stiffrock.chat.fragments.main.LogInFragment;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.utils.FragmentContainerActivity;
import com.stiffrock.chat.utils.SecureStorage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends FragmentContainerActivity {

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleAutoLogin();
    }

    private void handleAutoLogin() {
        SharedPreferences sp = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        boolean rememberLogIn = sp.getBoolean("rememberLogIn", false);

        if (rememberLogIn) {
            SecureStorage ss = new SecureStorage();
            LoginDTO credentials = ss.getUserCredentials(this);
            if (credentials.getUsername() == null || credentials.getPassword() == null) {
                sp.edit().putBoolean("rememberLogIn", false).apply();
                Toast.makeText(this, "Error, por favor inica sesión manualmente", Toast.LENGTH_SHORT).show();
                return;
            }

            Call<User> call = RetrofitClient.getApiService().logInUser(credentials);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        CurrentUser.setCurrentUser(user);
                        navigateToHomeActivity();
                    } else {
                        sp.edit().putBoolean("rememberLogIn", false).apply();
                        Toast.makeText(getBaseContext(), "Error, por favor inica sesión manualmente", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    Toast.makeText(getBaseContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            replaceFragment(new LogInFragment());
        }
    }

    public void navigateToHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}