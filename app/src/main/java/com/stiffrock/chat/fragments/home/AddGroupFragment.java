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
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.utils.ApiCallback;

import java.util.ArrayList;
import java.util.Collections;
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
        participants = Collections.singletonList(CurrentUser.getCurrentUser());

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

        apiCheckIfUserExists(name, (success, userDto) -> {
            if (userDto == null) {
                Toast.makeText(requireContext(), "El usuario introducido no existe", Toast.LENGTH_SHORT).show();
                return;
            }

            contactCardItems.add(new ItemContactCard(name));
            participants.add(userDto);
            adapter.notifyItemInserted(contactCardItems.size() - 1);
        });
    }

    private void apiCheckIfUserExists(String username, ApiCallback<UserDTO> callback) {
        Call<UserDTO> call = apiService.getUserByUsername(username);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserDTO> call, @NonNull Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(true, response.body());
                } else {
                    callback.onResult(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDTO> call, @NonNull Throwable t) {
                Log.e(TAG, "GetUserByUsername request failed: " + t.getMessage());
                callback.onResult(false, null);
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

        apiService.createGroupChat(new CreateGroupRequest(groupName, participants));
    }
}