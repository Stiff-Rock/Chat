package com.stiffrock.chat.fragments.home;

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
import com.stiffrock.chat.network.WebSocketClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGroupFragment extends Fragment {
    private EditText etGroupName, etUser;

    private MyAdapter adapter;
    private List<Item> contactCardItems;
    private Set<Long> participants;

    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_group, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        contactCardItems = new ArrayList<>();
        participants = new HashSet<>();
        participants.add(CurrentUser.getCurrentUser().getId());

        adapter = new MyAdapter(contactCardItems);
        recyclerView.setAdapter(adapter);

        etGroupName = view.findViewById(R.id.etGroupName);
        etUser = view.findViewById(R.id.etUser);
        view.findViewById(R.id.btnAddUser).setOnClickListener(v -> addParticipant());
        view.findViewById(R.id.btnCreateGroup).setOnClickListener(v -> createGroup());

        apiService = RetrofitClient.getApiService();

        return view;
    }

    private void addParticipant() {
        String name = etUser.getText().toString().trim();

        if (name.isBlank()) {
            Toast.makeText(requireContext(), "Introduce un nombre de usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.equals(CurrentUser.getCurrentUser().getUsername())) {
            Toast.makeText(requireContext(), "No hace falta introducir tu propio usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        // Comprueba si el usuario introducido existe en la base de datos
        Call<User> call = apiService.getUserByUsername(name);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long userId = response.body().getId();
                    if (!participants.contains(userId)) {
                        contactCardItems.add(new ItemContactCard(name));
                        participants.add(userId);
                        adapter.notifyItemInserted(contactCardItems.size() - 1);
                        etUser.setText("");
                    } else {
                        Toast.makeText(requireContext(), "Usuario ya presente en el grupo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "El usuario introducido no existe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG, "GetUserByUsername request failed: " + t.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
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
        GroupChatDTO gcd = new GroupChatDTO(groupName, participants, userId);

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
}