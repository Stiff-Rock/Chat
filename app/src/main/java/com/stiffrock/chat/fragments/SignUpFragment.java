package com.stiffrock.chat.fragments;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.stiffrock.chat.MainActivity;
import com.stiffrock.chat.R;
import com.stiffrock.chat.model.ApiResponse;
import com.stiffrock.chat.model.ApiService;
import com.stiffrock.chat.model.RetrofitClient;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.utils.ApiCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpFragment extends Fragment {
    private EditText etUsername, etPassword1, etPassword2;

    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        etUsername = view.findViewById(R.id.etUsername);
        etPassword1 = view.findViewById(R.id.etSigInPwd1);
        etPassword2 = view.findViewById(R.id.etSigInPwd2);
        view.findViewById(R.id.btnSignIn).setOnClickListener(v -> handleSignIn());
        view.findViewById(R.id.LogInRedirect).setOnClickListener(v -> redirectToLogIn());

        apiService = RetrofitClient.getApiService();

        return view;
    }

    private void handleSignIn() {
        String inputUsername = etUsername.getText().toString().trim();
        String inputPassword1 = etPassword1.getText().toString().trim();
        String inputPassword2 = etPassword2.getText().toString().trim();

        if (inputUsername.isBlank() || inputPassword1.isBlank() || inputPassword2.isBlank()) {
            Toast.makeText(requireContext(), "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        } else if (!inputPassword1.equals(inputPassword2)) {
            Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(inputUsername, inputPassword1);

        apiRegister(user, (success, result) -> {
            if (success) {
                Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
                redirectToLogIn();
            } else {
                Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void apiRegister(User user, ApiCallback<String> callback) {
        Call<ApiResponse> call = apiService.registerUser(user);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Register successful: " + response.body().getMessage());
                    callback.onResult(true, response.body().getMessage());
                } else {
                    String errorMessage;
                    switch (response.code()) {
                        case 400:
                            errorMessage = "Bad Request: Invalid input.";
                            break;
                        case 409:
                            //TODO: CHECK WHY IT DOES THIS
                            
                            // Conflict error, for example: username already exists or empty password
                            if (response.body() != null) {
                                errorMessage = response.body().getMessage(); // Extract message from the body
                            } else {
                                errorMessage = "Conflict: User already exists or password is empty.";
                            }
                            break;
                        case 422:
                            errorMessage = "Unprocessable Entity: Invalid data provided.";
                            break;
                        default:
                            errorMessage = "Register failed, please try again.";
                            break;
                    }
                    Log.e(TAG, "Register failed: " + response.code() + "\n" + errorMessage);
                    callback.onResult(false, errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Register request failed: " + t.getMessage());
                callback.onResult(false, "Conecction error");
            }
        });
    }

    private void redirectToLogIn() {
        ((MainActivity) requireActivity()).replaceFragment(new LogInFragment());
    }
}