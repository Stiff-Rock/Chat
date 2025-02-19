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

import com.google.gson.Gson;
import com.stiffrock.chat.MainActivity;
import com.stiffrock.chat.R;
import com.stiffrock.chat.dto.CredentialsDTO;
import com.stiffrock.chat.dto.ApiResponse;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.network.WebSocketClient;
import com.stiffrock.chat.utils.SecureStorage;

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

        CredentialsDTO credentials = new CredentialsDTO(inputUsername, inputPassword);

        //TODO: IMPROVE USER FEEDBACK
        Call<User> call = apiService.logInUser(credentials);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    CurrentUser.setCurrentUser(user);

                    WebSocketClient.getInstance().connect();

                    Log.d(TAG, "Login successful");
                    Toast.makeText(requireContext(), "Inicio de sesión correcto", Toast.LENGTH_SHORT).show();

                    SharedPreferences sp = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                    sp.edit().putBoolean("rememberLogIn", ckbxRememberMe.isChecked()).apply();

                    SecureStorage ss = new SecureStorage();
                    if (ckbxRememberMe.isChecked()) {
                        ss.saveUserCredentials(requireContext(), credentials);
                    } else {
                        ss.saveUserCredentials(requireContext(), new CredentialsDTO());
                    }
                    ((MainActivity) requireActivity()).navigateToHomeActivity();
                } else {
                    String errorMessage = "Login failed, please try again.";
                    try {
                        if (response.errorBody() != null) {
                            ApiResponse errorResponse = new Gson().fromJson(response.errorBody().string(), ApiResponse.class);
                            errorMessage = errorResponse.getMessage();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }

                    Log.e(TAG, "Login failed: " + response.code() + " - " + errorMessage);
                    Toast.makeText(requireContext(), "Error al iniciar sesión. Verifica tus credenciales.", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG, "Login request failed: " + t.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        ((MainActivity) requireActivity()).replaceFragment(fragment);
    }
}