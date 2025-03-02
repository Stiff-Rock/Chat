package com.stiffrock.chat.fragments.home;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.stiffrock.chat.ChatActivity;
import com.stiffrock.chat.HomeActivity;
import com.stiffrock.chat.R;
import com.stiffrock.chat.adapters.MyAdapter;
import com.stiffrock.chat.dto.ApiResponse;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemChatCard;
import com.stiffrock.chat.model.BaseChat;
import com.stiffrock.chat.utils.CurrentUser;
import com.stiffrock.chat.model.GroupChat;
import com.stiffrock.chat.model.Message;
import com.stiffrock.chat.model.PrivateChat;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.model.WebSocketAction;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.network.WebSocketClient;
import com.stiffrock.chat.network.WebSocketNotification;
import com.stiffrock.chat.utils.GsonManager;
import com.stiffrock.chat.utils.OnItemClickListener;
import com.stiffrock.chat.utils.WebSocketNotificationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Clase del fragment que muestra los contactos y grupos a los que pertenece el usuario y gestionarlos.
 */
public class ContactsFragment extends Fragment implements OnItemClickListener, WebSocketNotificationListener {
    private ApiService apiService;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<Item> chats;
    public Map<User, ItemChatCard> privateChatsMap;
    public Map<BaseChat, ItemChatCard> allChatsMap;

    private WebSocketClient wsClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        apiService = RetrofitClient.getApiService();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        wsClient = WebSocketClient.getInstance();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        CurrentUser.setCurrentChat(null);
        wsClient.setOnNotificationReceivedListener(this);
        apiGetChatList();
    }

    /**
     * Carga la lista de chats que este usuario tiene asociados en la BBDD mediante una solicitud
     * a la api y los carga en el RecyclerView
     */
    private void apiGetChatList() {
        chats = new ArrayList<>();
        privateChatsMap = new HashMap<>();
        allChatsMap = new HashMap<>();

        Call<List<BaseChat>> call = apiService.getUserChats(CurrentUser.getCurrentUser().getId());
        call.enqueue(new Callback<List<BaseChat>>() {
            @Override
            public void onResponse(@NonNull Call<List<BaseChat>> call, @NonNull Response<List<BaseChat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (BaseChat chat : response.body()) {
                        ItemChatCard icc = new ItemChatCard(chat);
                        chats.add(icc);
                        if (chat instanceof PrivateChat) {
                            User contact = ((PrivateChat) chat).getContact(CurrentUser.getCurrentUser());
                            privateChatsMap.put(contact, icc);
                            CurrentUser.getContacts().clear();
                            CurrentUser.getContacts().add((PrivateChat) chat);
                        }

                        allChatsMap.put(chat, icc);
                    }

                    adapter = new MyAdapter(chats, ContactsFragment.this);
                    recyclerView.setAdapter(adapter);

                    wsGetContactsStatus();
                } else {
                    Toast.makeText(requireContext(), "No se han encontrado contactos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<BaseChat>> call, @NonNull Throwable throwable) {
                Log.e(TAG, "GetUserChats request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();

                adapter = new MyAdapter(chats, ContactsFragment.this);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    /**
     * Envia un mensaje al WebSocket para que este devuelta el status de los contactos del usuario
     */
    private void wsGetContactsStatus() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", WebSocketAction.GET_CONTACTS_ONLINE_STATUS.name());
        String currentUserJson = GsonManager.gson.toJson(CurrentUser.getCurrentUser());
        jsonObject.add("content", GsonManager.gson.fromJson(currentUserJson, JsonObject.class));
        wsClient.sendMessage(jsonObject.toString());
    }

    /**
     * Envia un mensaje al WebSocket para que este devuela el status de un usuario concreto
     *
     * @param user Usuario del que se solicita saber el status
     */
    private void wsGetUserStatus(User user) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", WebSocketAction.GET_USER_ONLINE_STATUS.name());
        String userToCheck = GsonManager.gson.toJson(user);
        jsonObject.add("content", GsonManager.gson.fromJson(userToCheck, JsonObject.class));
        wsClient.sendMessage(jsonObject.toString());
    }

    /**
     * Añade un nuevo contacto, lo muestra en el RecyclerView y solicita su status
     *
     * @param chat Chat que se acaba de crear conn dicho usuario
     * @param user Usuario con el que se está establenciendo un nuevo chato
     */
    public void addContact(BaseChat chat, User user) {
        ItemChatCard icc = new ItemChatCard(chat);
        chats.add(icc);
        if (chat instanceof PrivateChat) {
            privateChatsMap.put(user, icc);
            wsGetUserStatus(user);
        }

        allChatsMap.put(chat, icc);

        adapter.notifyItemInserted(chats.size() - 1);
    }

    /**
     * Añade un nuevo chat al RecyclerView.
     * Este método es llamado por el listener de notificaciones de WebSocket.
     *
     * @param chat Chat al que el usuario ha sido añadido
     */
    private void addChat(BaseChat chat) {
        ItemChatCard icc = new ItemChatCard(chat);
        chats.add(icc);
        allChatsMap.put(chat, icc);
        adapter.notifyItemInserted(chats.size() - 1);

        String type = null;
        if (chat instanceof PrivateChat) {
            type = "contacto";
            User contact = ((PrivateChat) chat).getContact(CurrentUser.getCurrentUser());
            privateChatsMap.put(contact, icc);
            wsGetContactsStatus();
        } else if (chat instanceof GroupChat) {
            type = "grupo";
        }

        Toast.makeText(requireContext(), "Se ha añadido un nuevo " + type, Toast.LENGTH_SHORT).show();
    }

    /**
     * Elimina un chat del RecyclerView.
     * Este método es llamado por el listener de notificacione de WebSocket.
     *
     * @param chat Chat que se va a eliminar
     */
    private void deleteChat(BaseChat chat) {
        int index;
        ItemChatCard icc;
        String chatName;
        String type;
        if (chat instanceof PrivateChat) {
            User contact = ((PrivateChat) chat).getContact(CurrentUser.getCurrentUser());
            icc = privateChatsMap.get(contact);
            index = chats.indexOf(icc);
            privateChatsMap.remove(contact);
            chatName = ((PrivateChat) chat).getContact(CurrentUser.getCurrentUser()).getUsername();
            type = "contacto";
        } else if (chat instanceof GroupChat) {
            icc = allChatsMap.get(chat);
            index = chats.indexOf(icc);
            chatName = ((GroupChat) chat).getName();
            type = "grupo";
        } else {
            Log.wtf(TAG, "Provided BaseChat is wrong type or null: " + chat);
            return;
        }

        allChatsMap.remove(chat);
        chats.remove(icc);
        adapter.notifyItemRemoved(index);
        Toast.makeText(requireContext(), "Se ha eliminado el " + type + " \"" + chatName + "\"", Toast.LENGTH_SHORT).show();
    }

    /**
     * Método que envía una solicitud al servidor para eliminar a un contacto.
     *
     * @param contactId Id del chat que se tiene con dicho contacto
     */
    private void apiDeleteContact(Long contactId) {
        Call<ApiResponse> call = apiService.deleteContact(contactId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (!response.isSuccessful() && response.body() == null) {
                    Log.e(TAG, "Error deleting contact: " + response.code());
                    Toast.makeText(requireContext(), "Error eliminando el contacto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.e(TAG, "DeleteContact request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Abre el context menu con las acciones posbiles relacionadas con el contacto seleccionado
     *
     * @param view View el item que se ha seleccionado
     * @param item Item del RecyclerView que se ha seleccionado
     */
    private void openContactContextMenuPopup(View view, ItemChatCard item) {
        BaseChat chat = item.getChat();
        if (!(chat instanceof PrivateChat)) return;

        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.contact_context_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.deleteContact) {
                apiDeleteContact(chat.getId());
            }
            return true;
        });

        popupMenu.show();
    }

    /**
     * Muestra una notificación en forma de Toast de un chat del que se ha recibido un mensaje
     * y reorganizan los item del recycler en funcion del mensaje del contacto más reciente.
     *
     * @param msg Mensaje que se ha recibido
     */
    private void showNotification(Message msg) {
        if (msg.getSender().getUsername().equals("SYSTEM")) return;

        BaseChat chat = msg.getChat();

        String chatName;
        if (chat instanceof PrivateChat)
            chatName = ((PrivateChat) chat).getContact(CurrentUser.getCurrentUser()).getUsername();
        else if (chat instanceof GroupChat) chatName = ((GroupChat) chat).getName();
        else return;

        Toast.makeText(requireContext(), "Mensaje recibido de " + chatName, Toast.LENGTH_SHORT).show();

        ItemChatCard icc = privateChatsMap.get(msg.getSender());
        int lastIndex = chats.indexOf(icc);

        if (lastIndex == -1) return;

        boolean isDeleted = chats.remove(icc);
        if (!isDeleted) {
            Log.e(TAG, "Could not delete chatCard for user while triying to move it to the top <" + msg.getSender().getUsername() + ">");
            return;
        }
        chats.add(0, icc);
        adapter.notifyItemMoved(lastIndex, 0);
    }

    /**
     * Actualiza el online status de un item del RecyclerVew asociado con un contacto.
     * Este método es llamado por el listener de notificacione de WebSocket.
     *
     * @param isOnline Si el usuario esta online o no.
     * @param user     El usuario en cuestión.
     */
    private void updateContactOnlineStatus(boolean isOnline, User user) {
        if (adapter == null) return;

        ItemChatCard icc = privateChatsMap.get(user);
        if (icc == null) {
            Log.w(TAG, "Could not retireve contact ChatCard:\nUser: " + user);
            return;
        }
        icc.setOnline(isOnline);
        int index = chats.indexOf(icc);
        adapter.notifyItemChanged(index);
    }

    /* Listeners personalizados para gestionar el click o la selección de elementos del RecyclerView
    que contiene los chats */
    @Override
    public void onItemClick(View view, Item item, int postion) {
        CurrentUser.setCurrentChat(((ItemChatCard) item).getChat());
        ((HomeActivity) requireActivity()).navigateToActivity(ChatActivity.class);
    }

    @Override
    public void onLongItemClick(View view, Item item, int postion) {
        openContactContextMenuPopup(view, (ItemChatCard) item);
    }

    /**
     * Listener de notificaciones recibidas por el WebSocket. Se determina que se debe realizar en
     * función del contenido del JSON de notificación recibido.
     *
     * @param notification Contenido en formato Json de la notificacion que va a ser parseado
     *                     por la utility class de {@link WebSocketNotification}.
     *
     * @see WebSocketNotificationListener
     * @see WebSocketNotification
     * @see WebSocketAction
     */
    @Override
    public void onNotificationReceived(String notification) {
        WebSocketNotification wsn = WebSocketNotification.parse(notification);
        switch (wsn.getAction()) {
            case MESSAGE_RECEIVED:
                Message msg = (Message) wsn.getContent();
                showNotification(msg);
                break;
            case ADD_CHAT:
                BaseChat addChat = (BaseChat) wsn.getContent();
                addChat(addChat);
                break;
            case DELETE_CONTACT:
            case DELETE_GROUP:
                BaseChat deletedChat = (BaseChat) wsn.getContent();
                deleteChat(deletedChat);
                break;
            case USER_CONNECTED:
                User userConnected = (User) wsn.getContent();
                updateContactOnlineStatus(true, userConnected);
                break;
            case USER_DISCONNECTED:
                User userDisconnected = (User) wsn.getContent();
                updateContactOnlineStatus(false, userDisconnected);
                break;
            default:
                break;
        }
    }
}