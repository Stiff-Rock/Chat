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
import com.stiffrock.chat.dto.UserDTO;
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


        //TODO: DESHACER EL CALLBACK HACERLO TODO DIRECTMENTE AHI
        apiLogIn(user, (success, userDto) -> {
            if (success && userDto != null) {
                CurrentUser.setCurrentUser(userDto);
                WebSocketClient.getInstance().connect();
                ((MainActivity) requireActivity()).navigateToHomeActivity();

                Toast.makeText(requireContext(), "Inicio de sesión correcto", Toast.LENGTH_SHORT).show();

                SharedPreferences sp = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                sp.edit().putBoolean("rememberLogIn", ckbxRememberMe.isChecked()).apply();

                if (ckbxRememberMe.isChecked()) {
                    sp.edit().putLong("storedUserId", userDto.getId()).apply();
                    sp.edit().putString("storedUserName", userDto.getUsername()).apply();
                } else {
                    sp.edit().putLong("storedUserId", -1).apply();
                    sp.edit().putString("storedUserName", "").apply();
                }
            } else {
                Toast.makeText(requireContext(), "Error al iniciar sesión. Verifica tus credenciales.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //TODO: MEJORAR EL FEEDBACK AL USUARIO
    private void apiLogIn(User user, ApiCallback<UserDTO> callback) {
        Call<UserDTO> call = apiService.logInUser(user);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserDTO> call, @NonNull Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDTO userDto = response.body();
                    Log.d(TAG, "Login successful");
                    callback.onResult(true, userDto);
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
                    callback.onResult(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDTO> call, @NonNull Throwable t) {
                Log.e(TAG, "Login request failed: " + t.getMessage());
                callback.onResult(false, null);
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        ((MainActivity) requireActivity()).replaceFragment(fragment);
    }
}