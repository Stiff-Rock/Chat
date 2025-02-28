package com.stiffrock.chat.fragments.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.stiffrock.chat.R;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.utils.ImageManager;

public class UserProfileFragment extends Fragment {
    private ApiService apiService;

    private ImageView ivUserPfp;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        User currentUser = CurrentUser.getCurrentUser();

        apiService = RetrofitClient.getApiService();

//        ivUserPfp = view.findViewById(R.id.ivUserPfp);
//        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                Uri selectedImageUri = result.getData().getData();
//                if (ImageManager.isValidImage(requireActivity(), selectedImageUri)) {
//                    ivUserPfp.setImageURI(selectedImageUri);
//                }
//            }
//        });
//        groupPfp = view.findViewById(R.id.groupPfp);
//        groupPfp.setOnClickListener(v -> ImageManager.openGallery(pickImageLauncher));

        EditText tvUsername = view.findViewById(R.id.tvUsername);
        tvUsername.setText(currentUser.getUsername());

        EditText tvUserId = view.findViewById(R.id.tvUserId);
        tvUserId.setText(view.getContext().getString(R.string.user_id, String.valueOf(currentUser.getId())));

        return view;
    }
}