package com.stiffrock.chat.fragments.home;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stiffrock.chat.HomeActivity;
import com.stiffrock.chat.R;
import com.stiffrock.chat.adapters.MyAdapter;
import com.stiffrock.chat.dto.ApiResponse;
import com.stiffrock.chat.dto.GroupChatDTO;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemContactCard;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.utils.ImageManager;
import com.stiffrock.chat.utils.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGroupFragment extends Fragment implements OnItemClickListener {
    private EditText etGroupName;

    private ImageView groupPfp = null;
    private Uri currentImageUri;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    private MyAdapter adapter;

    private List<Item> contactCardItems;
    private Set<Long> participants;
    private final List<User> contacts;

    private ApiService apiService;

    public AddGroupFragment(List<User> contacts) {
        this.contacts = contacts;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_group, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        contactCardItems = new ArrayList<>();
        participants = new HashSet<>();
        participants.add(CurrentUser.getCurrentUser().getId());

        etGroupName = view.findViewById(R.id.etGroupName);
        view.findViewById(R.id.btnCreateGroup).setOnClickListener(v -> createGroup());

        groupPfp = view.findViewById(R.id.groupPfp);
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Toast.makeText(view.getContext(), "Imagen seleccionada, porfavor espere a que cargue...", Toast.LENGTH_SHORT).show();
                Uri selectedImageUri = result.getData().getData();
                if (ImageManager.isValidImage(requireActivity(), selectedImageUri)) {
                    currentImageUri = selectedImageUri;
                    groupPfp.setImageURI(selectedImageUri);
                }
            }
        });
        groupPfp.setOnClickListener(v -> ImageManager.openGallery(requireActivity(), pickImageLauncher));

        apiService = RetrofitClient.getApiService();

        for (User user : contacts) {
            contactCardItems.add(new ItemContactCard(user, false, false));
        }

        adapter = new MyAdapter(contactCardItems, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pickImageLauncher != null) {
            pickImageLauncher.unregister();
        }
    }

    private void createGroup() {
        String groupName = etGroupName.getText().toString().trim();
        if (groupName.isBlank()) {
            Toast.makeText(requireContext(), "Introduce un nombre de grupo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contactCardItems.isEmpty()) {
            Toast.makeText(requireContext(), "Debes elegir al menos 1 miembro para crear el grupo", Toast.LENGTH_SHORT).show();
            return;
        }

        Long userId = CurrentUser.getCurrentUser().getId();

        if (currentImageUri != null) {
            ImageManager.apiUploadImage(requireContext(), currentImageUri, fotoUrl -> {
                GroupChatDTO gcd = new GroupChatDTO(groupName, participants, userId, fotoUrl);
                apiCreateGroup(gcd);
            });
        } else {
            GroupChatDTO gcd = new GroupChatDTO(groupName, participants, userId, null);
            apiCreateGroup(gcd);
        }
    }

    private void apiCreateGroup(GroupChatDTO gcd) {
        Call<ApiResponse> call = apiService.createGroupChat(gcd);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), "Grupo creado correctamente", Toast.LENGTH_SHORT).show();
                    ((HomeActivity) requireActivity()).replaceFragment(new ContactsFragment());
                } else {
                    Toast.makeText(requireContext(), "Error creando grupo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Creating group request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(View view, Item item, int postion) {
        ItemContactCard icc = (ItemContactCard) item;
        Long userId = icc.getUser().getId();
        if (participants.contains(userId)) {
            participants.remove(userId);
            icc.setSelected(false);
        } else {
            participants.add(userId);
            icc.setSelected(true);
        }
        adapter.notifyItemChanged(postion);
    }

    @Override
    public void onLongItemClick(View view, Item item, int postion) {

    }
}