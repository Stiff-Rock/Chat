package com.stiffrock.chat.fragments.chat;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stiffrock.chat.ChatActivity;
import com.stiffrock.chat.R;
import com.stiffrock.chat.adapters.MyAdapter;
import com.stiffrock.chat.dto.ApiResponse;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemContactCard;
import com.stiffrock.chat.utils.CurrentUser;
import com.stiffrock.chat.model.GroupChat;
import com.stiffrock.chat.model.PrivateChat;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.model.WebSocketAction;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.utils.ImageManager;
import com.stiffrock.chat.utils.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Clase del fragment que muestra la información del chat grupal en el que se encuentra el usuario
 */
public class GroupChatInfoFragment extends Fragment implements OnItemClickListener {
    private GroupChat chat;

    private ImageView ivContactPhoto;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<Item> contactItems;
    private Map<User, ItemContactCard> userItemContactCardMap;

    private ApiService apiService;

    private boolean isRecyclerLoaded;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat_info, container, false);

        chat = (GroupChat) CurrentUser.getCurrentChat();

        apiService = RetrofitClient.getApiService();

        TextView tvChatName = view.findViewById(R.id.tvChatName);
        tvChatName.setText(chat.getName());

        ivContactPhoto = view.findViewById(R.id.ivContactPhoto);

        String photo = chat.getChatPhotoUrl();
        if (photo != null) {
            ImageManager.setImageViewPhoto(view.getContext(), ivContactPhoto, photo, null);
        }

        // Confirurar el launcher para elegir una foto de la galería para establecerlo como foto del grupo
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Toast.makeText(view.getContext(), "Imagen seleccionada, porfavor espere a que cargue...", Toast.LENGTH_SHORT).show();
                Uri selectedImageUri = result.getData().getData();
                if (ImageManager.isValidImage(requireActivity(), selectedImageUri)) {
                    ImageManager.apiUploadImage(requireContext(), selectedImageUri, fotoUrl -> ImageManager.setImageViewPhoto(view.getContext(), ivContactPhoto, fotoUrl, sucess -> {
                        if (sucess) apiUpdateGroupChatPorfilePicture(fotoUrl);
                    }));
                }
            }
        });
        ivContactPhoto.setOnClickListener(v -> ImageManager.openGallery(requireActivity(), pickImageLauncher));

        view.findViewById(R.id.btnAddMember).setOnClickListener(v -> addMemberDialog());
        view.findViewById(R.id.btnLeaveGroup).setOnClickListener(v -> {
            boolean isAdmin = chat.getAdmins().contains(CurrentUser.getCurrentUser());
            boolean onlyOneAdmin = chat.getAdmins().size() == 1;
            boolean hasOtherParticipants = chat.getParticipants().size() > 1;
            if (isAdmin && onlyOneAdmin && hasOtherParticipants) {
                Toast.makeText(requireContext(), "Designa otro admin antes de abandonar el grupo", Toast.LENGTH_SHORT).show();
                return;
            }
            leaveGroupDialog();
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        apiGetChatInfo();
    }

    /**
     * Envía una solicitud al servidor para obtener la información sobre el chat almacenada en la BBDD
     * y la carga en la vista.
     */
    private void apiGetChatInfo() {
        isRecyclerLoaded = false;

        Call<GroupChat> call = apiService.getGroupChat(chat.getId());
        call.enqueue(new Callback<GroupChat>() {
            @Override
            public void onResponse(@NonNull Call<GroupChat> call, @NonNull Response<GroupChat> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chat = response.body();
                    initAdapter();
                } else {
                    Log.e(TAG, "Error updating GroupChat information: " + response.code());
                    Toast.makeText(requireContext(), "Error actualizando información del grupo", Toast.LENGTH_SHORT).show();
                    initAdapter();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GroupChat> call, @NonNull Throwable throwable) {
                Log.e(TAG, "GetGroupChat request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                initAdapter();
            }
        });
    }

    /**
     * Inicializa el adapter del RecyclerView que contiene la lista de participantes del grupo.
     */
    private void initAdapter() {
        contactItems = new ArrayList<>();
        userItemContactCardMap = new HashMap<>();
        for (User user : chat.getParticipants()) {
            ItemContactCard icc = new ItemContactCard(user, false, false);

            if (user.equals(CurrentUser.getCurrentUser())) icc.setName("(Tú)");

            contactItems.add(icc);
            userItemContactCardMap.put(user, icc);

            if (chat.getAdmins().contains(user)) icc.setName("[ADMIN] " + icc.getName());
        }
        adapter = new MyAdapter(contactItems, this);
        recyclerView.setAdapter(adapter);
        isRecyclerLoaded = true;
    }

    /**
     * Muestra un popup con las acciones posibles relacionadas conn la gestion de los miembros del
     * grupo, caso de ser admin y la posiblidad de ver la infomación de contacto del integrante
     * seleciconado.
     *
     * @param item    Item del RecyclerView seleccionado
     * @param view    View del item del RecyclerView
     * @param postion Posicion en la que se encuentra el item en el RecyclerView
     */
    public void showOptionsContextMenu(ItemContactCard item, View view, int postion) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.group_chat_context_menu, popupMenu.getMenu());
        Menu menu = popupMenu.getMenu();

        User selecterUser = item.getUser();
        if (chat.getAdmins().contains(CurrentUser.getCurrentUser())) {
            boolean selectedUserisAdmin = chat.getAdmins().contains(selecterUser);
            menu.findItem(R.id.addAdmin).setVisible(!selectedUserisAdmin);
            menu.findItem(R.id.removeAdmin).setVisible(selectedUserisAdmin);
        } else {
            menu.findItem(R.id.addAdmin).setVisible(false);
            menu.findItem(R.id.removeAdmin).setVisible(false);
            menu.findItem(R.id.removeFromGroupChat).setVisible(false);
        }

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (!isRecyclerLoaded) return true;

            if (menuItem.getItemId() == R.id.addAdmin) {
                apiAddAdmin(selecterUser);
            } else if (menuItem.getItemId() == R.id.removeAdmin) {
                apiRemoveAdmin(selecterUser);
            } else if (menuItem.getItemId() == R.id.removeFromGroupChat) {
                apiRemoveMember(selecterUser);
            } else if (menuItem.getItemId() == R.id.showUserInfo) {
                navigateToUserInfoFragment(item.getUser());
            }

            item.setSelected(false);
            adapter.notifyItemChanged(postion);
            return true;
        });

        popupMenu.show();
    }

    /**
     * Muestra un popup cuando se presiona el botón de "Añadir miembro" que muestra un RecyclerView
     * con la lista de contactos del usuario para añadir a alguno de ellos al grupo.
     */
    private void addMemberDialog() {
        if (!isRecyclerLoaded) return;

        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.contacts_popup, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Filtrar contactos que no están en el grupo
        List<PrivateChat> contacts = CurrentUser.getContacts().stream().filter(pc -> !chat.getParticipants().contains(pc.getContact(CurrentUser.getCurrentUser()))).collect(Collectors.toList());

        List<Item> contactItems = new ArrayList<>();
        for (PrivateChat pc : contacts) {
            User user = pc.getContact(CurrentUser.getCurrentUser());
            ItemContactCard icc = new ItemContactCard(user, false, false);
            contactItems.add(icc);
        }

        MyAdapter adapter = new MyAdapter(contactItems, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, Item item, int postion) {
                ItemContactCard icc = (ItemContactCard) item;
                apiAddMember(icc.getUser());
                dialog.dismiss();
            }

            @Override
            public void onLongItemClick(View view, Item item, int postion) {
            }
        });

        recyclerView.setAdapter(adapter);
        view.findViewById(R.id.btnCancel).setOnClickListener(e -> dialog.dismiss());
        dialog.show();
    }

    /**
     * Muestra un popup para confirmar si se quiere abandonar el grupo
     */
    private void leaveGroupDialog() {
        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = 30;
        layout.setPadding(padding, padding, padding, padding);

        int textColor = context.getColor(R.color.standard_text_color_tertiary);

        SpannableString title = new SpannableString("¿Estás seguro de que quieres abandonar el grupo?");
        title.setSpan(new ForegroundColorSpan(textColor), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setTitle(title).setView(layout).setPositiveButton("Aceptar", (dialog, which) -> apiRemoveMember(CurrentUser.getCurrentUser())).setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        int backgroundColor = context.getColor(R.color.md_theme_dark_primaryContainer);

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (positiveButton != null) positiveButton.setTextColor(textColor);


        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (negativeButton != null) negativeButton.setTextColor(textColor);


        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
    }

    /**
     * Envía una solicitud al servidor para añadir un nuevo usuario al grupo
     *
     * @param user Usuario a añadir al grupo
     */
    private void apiAddMember(User user) {
        Call<ApiResponse> addMember = apiService.addMember(chat.getId(), user.getId());
        addMember.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Toast.makeText(requireContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Error adding member: " + response.code());
                    Toast.makeText(requireContext(), "Error añadiendo miembro", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.e(TAG, "AddMember request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Envía una solicitud al servidor para eliminar a un usuario del grupo
     *
     * @param user Usuario a eliminar del grupo
     */
    private void apiRemoveMember(User user) {
        Call<ApiResponse> removeMember = apiService.removeMember(chat.getId(), user.getId());
        removeMember.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (user.equals(CurrentUser.getCurrentUser())) {
                        Toast.makeText(requireContext(), "Has abandonado el grupo", Toast.LENGTH_SHORT).show();
                        ((ChatActivity) requireActivity()).navigateToHomeActivity();
                    } else
                        Toast.makeText(requireContext(), user.getUsername() + " ha sido expulsado", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Error removing member: " + response.code());
                    if (user.equals(CurrentUser.getCurrentUser()))
                        Toast.makeText(requireContext(), "Error abandonado grupo", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(requireContext(), "Error expulsando miembro", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.e(TAG, "ApiRemoveMember request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Envia una solicitud al servidor para establecer a un miembro como administrador del grupo
     *
     * @param user Usuario que se va a establecer como admin
     */
    private void apiAddAdmin(User user) {
        Call<ApiResponse> addAdmin = apiService.addAdmin(chat.getId(), user.getId());
        addAdmin.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), user.getUsername() + " ahora es administrador", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Error making admin: " + response.code());
                    Toast.makeText(requireContext(), "Error designando administrador", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.e(TAG, "AddAdmin request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Envia una solicitud al servidor para eliminar privilegios de administrador de un miembro
     *
     * @param user Usuario al que se le van a revocar sus priviledios de administrador
     */
    private void apiRemoveAdmin(User user) {
        Call<ApiResponse> removeAdmin = apiService.removeAdmin(chat.getId(), user.getId());
        removeAdmin.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), user.getUsername() + " ya no es administrador", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Error removing admin: " + response.code());
                    Toast.makeText(requireContext(), "Error eliminando administrador", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.e(TAG, "RemoveAdmin request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Envia una solicitud al servido para actualiar la foto del grupo
     *
     * @param imageUrl Url de la foto subira al servidor
     */
    private void apiUpdateGroupChatPorfilePicture(String imageUrl) {
        Long chatId = chat.getId();
        Call<ApiResponse> call = apiService.updateGroupChatPhoto(chatId, imageUrl);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chat.setChatPhotoUrl(imageUrl);
                    Toast.makeText(requireContext(), "Foto del grupo actualizada!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Error updating gorup chat photo: " + response.code());
                    Toast.makeText(requireContext(), "Error actualizando foto del grupo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.e(TAG, "UpdateGroupChatPorfilePicture request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Navega al fragment que muestra la información de un usuario seleccionado
     *
     * @param user Usuario del que se quiere ver la información
     */
    private void navigateToUserInfoFragment(User user) {
        ChatActivity ca = (ChatActivity) getActivity();
        if (ca != null) ca.replaceFragment(new ContactInfoFragment(user));
    }

    /**
     * Método que actualiza la información de los miembros grupo.
     * <p>
     * Este método es llamado por el listener de notificaciones de WebSocket.
     *
     * @param action Acción que se ha realizado
     * @param chat   Referencia el chat grupal
     * @param user   Usuario que se ha visto afectado por el cambio
     */
    public void updateChatMembersInfo(WebSocketAction action, GroupChat chat, User user) {
        if (chat.getId().equals(this.chat.getId())) {
            this.chat = chat;

            ItemContactCard icc = userItemContactCardMap.get(user);
            int index = contactItems.indexOf(icc);
            contactItems.remove(icc);
            userItemContactCardMap.remove(user);
            if (action == WebSocketAction.GROUP_CHAT_CHANGED) {
                ItemContactCard newIcc = new ItemContactCard(user, false, false);

                if (user.equals(CurrentUser.getCurrentUser())) newIcc.setName("(Tú)");
                if (chat.getAdmins().contains(user)) newIcc.setName("[ADMIN] " + newIcc.getName());

                contactItems.add(newIcc);
                userItemContactCardMap.put(user, newIcc);
                adapter.notifyItemChanged(index);
            } else if (action == WebSocketAction.GROUP_CHAT_DELETION) {
                adapter.notifyItemRemoved(index);
            }
        }
    }

    // Listeners para gestionar el click o la seleccion de los items del RecyclerView de miembros
    @Override
    public void onItemClick(View view, Item item, int postion) {
        ItemContactCard icc = (ItemContactCard) item;
        navigateToUserInfoFragment(icc.getUser());
    }

    @Override
    public void onLongItemClick(View view, Item item, int postion) {
        ItemContactCard icc = (ItemContactCard) item;
        if (!icc.getUser().equals(CurrentUser.getCurrentUser())) {
            icc.setSelected(true);
            showOptionsContextMenu(icc, view, postion);
        }
    }
}