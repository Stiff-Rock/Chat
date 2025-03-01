package com.stiffrock.chat.fragments.home;

import static com.stiffrock.chat.network.ServerConfig.SOCKET_ADDR;
import static com.stiffrock.chat.utils.LogTag.TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.stiffrock.chat.R;
import com.stiffrock.chat.dto.ApiResponse;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.utils.ImageManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileFragment extends Fragment {
    private ImageView ivUserPfp;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        User currentUser = CurrentUser.getCurrentUser();

        ivUserPfp = view.findViewById(R.id.ivUserPfp);

        String photoUrl = currentUser.getProfilePictureUrl();
        if (photoUrl != null)
            ImageManager.setImageViewPhoto(view.getContext(), ivUserPfp, photoUrl, null);

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Toast.makeText(view.getContext(), "Imagen seleccionada, porfavor espere a que cargue...", Toast.LENGTH_SHORT).show();
                Uri selectedImageUri = result.getData().getData();
                if (ImageManager.isValidImage(requireActivity(), selectedImageUri)) {
                    ImageManager.apiUploadImage(requireContext(), selectedImageUri, fotoUrl -> {
                        fotoUrl = fotoUrl.replace("{ipAndPort}", SOCKET_ADDR);
                        String finalFotoUrl = fotoUrl;
                        ImageManager.setImageViewPhoto(view.getContext(), ivUserPfp, fotoUrl, sucess -> {
                            if (sucess) apiUpdateUserPorfilePicture(finalFotoUrl);
                        });
                    });
                }
            }
        });
        ivUserPfp.setOnClickListener(v -> ImageManager.openGallery(requireActivity(), pickImageLauncher));

        TextView tvUsername = view.findViewById(R.id.tvUsername);
        tvUsername.setText(currentUser.getUsername());

        TextView tvUserId = view.findViewById(R.id.tvUserId);
        tvUserId.setText(view.getContext().getString(R.string.user_id, String.valueOf(currentUser.getId())));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pickImageLauncher != null) {
            pickImageLauncher.unregister();
        }
    }

    private void apiUpdateUserPorfilePicture(String imageUrl) {
        Long userId = CurrentUser.getCurrentUser().getId();

        Call<ApiResponse> call = RetrofitClient.getApiService().updateUserPorfilePicture(userId, imageUrl);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CurrentUser.getCurrentUser().setProfilePictureUrl(imageUrl);
                    Toast.makeText(requireContext(), "Foto de perfil actualizada!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Error updating user profile picture: " + response.code());
                    Toast.makeText(requireContext(), "Error actualizando foto de perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.e(TAG, "UpdateUserPorfilePicture request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}