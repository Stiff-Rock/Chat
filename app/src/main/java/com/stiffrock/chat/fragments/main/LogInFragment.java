package com.stiffrock.chat.fragments.main;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.stiffrock.chat.MainActivity;
import com.stiffrock.chat.R;
import com.stiffrock.chat.model.ApiResponse;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.network.WebSocketClient;
import com.stiffrock.chat.utils.ApiCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInFragment extends Fragment {
    private EditText etUsername, etPassword;
    private CheckBox ckbxRememberMe;

    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPwd);
        ckbxRememberMe = view.findViewById(R.id.ckbxRememberMe);
        view.findViewById(R.id.btnLogin).setOnClickListener(v -> handleLogin());
        view.findViewById(R.id.sigInRedirect).setOnClickListener(v -> replaceFragment(new SignUpFragment()));

        apiService = RetrofitClient.getApiService();

        return view;
    }

    private void handleLogin() {
        String inputUsername = etUsername.getText().toString().trim();
        String inputPassword = etPassword.getText().toString().trim();

        if (inputUsername.isBlank() || inputPassword.isBlank()) {
            Toast.makeText(requireContext(), "Ingresa todas las credenciales", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(inputUsername, inputPassword);

        apiLogIn(user, (success, result) -> {
            if (success) {
                CurrentUser.setUsername(inputUsername);
                WebSocketClient.getInstance().connect();

                ((MainActivity) requireActivity()).navigateToHomeActivity();

                Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();

                SharedPreferences sp = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                sp.edit().putBoolean("rememberLogIn", ckbxRememberMe.isChecked()).apply();

                if (ckbxRememberMe.isChecked()) {
                    sp.edit().putString("storedUser", inputUsername).apply();
                } else {
                    sp.edit().putString("storedUser", "").apply();
                }
            } else {
                Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void apiLogIn(User user, ApiCallback<String> callback) {
        Call<ApiResponse> call = apiService.logInUser(user);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Login successful: " + response.body().getMessage());
                    callback.onResult(true, response.body().getMessage());
                } else {
                    String errorMessage;
                    switch (response.code()) {
                        case 400:
                            errorMessage = "Bad Request: Invalid input.";
                            break;
                        case 401:
                            errorMessage = "Error: Credenciales incorrectas";
                            break;
                        case 409:
                            errorMessage = "Conflict: Contraseña vacía.";
                            break;
                        default:
                            errorMessage = "Login failed, please try again.";
                            break;
                    }
                    Log.e(TAG, "Login failed: " + response.code() + "\n" + errorMessage);
                    callback.onResult(false, errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Login request failed: " + t.getMessage());
                callback.onResult(false, "Connection error");
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        ((MainActivity) requireActivity()).replaceFragment(fragment);
    }
}