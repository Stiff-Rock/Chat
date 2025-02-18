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

import com.stiffrock.chat.R;
import com.stiffrock.chat.adapters.MyAdapter;
import com.stiffrock.chat.dto.CreateGroupRequest;
import com.stiffrock.chat.dto.UserDTO;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemContactCard;
import com.stiffrock.chat.model.ApiResponse;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGroupFragment extends Fragment {
    private EditText etGroupName, etUser;

    private MyAdapter adapter;
    private List<Item> contactCardItems;
    private List<UserDTO> participants;

    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_group, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        contactCardItems = new ArrayList<>();
        participants = new ArrayList<>();
        participants.add(CurrentUser.getCurrentUser());

        adapter = new MyAdapter(contactCardItems);
        recyclerView.setAdapter(adapter);

        etGroupName = view.findViewById(R.id.etGroupName);
        etUser = view.findViewById(R.id.etUser);
        view.findViewById(R.id.btnAddUser).setOnClickListener(v -> addParticipant());
        view.findViewById(R.id.btnCreateGroup).setOnClickListener(v -> createGroup());

        apiService = RetrofitClient.getApiService();

        return view;
    }

    //TODO CHECK IF EXISTS CONTACT
    private void addParticipant() {
        String name = etUser.getText().toString().trim();

        if (name.isBlank()) {
            Toast.makeText(requireContext(), "Introduce un nombre de usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<UserDTO> call = apiService.getUserByUsername(name);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserDTO> call, @NonNull Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contactCardItems.add(new ItemContactCard(name));
                    participants.add(response.body());
                    adapter.notifyItemInserted(contactCardItems.size() - 1);
                    etUser.setText("");
                } else {
                    Toast.makeText(requireContext(), "El usuario introducido no existe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDTO> call, @NonNull Throwable t) {
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

        CreateGroupRequest cgr = new CreateGroupRequest(groupName, participants);

        Call<ApiResponse> call = apiService.createGroupChat(cgr);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {

            }
        });
    }
}