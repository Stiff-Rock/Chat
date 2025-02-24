package com.stiffrock.chat;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.stiffrock.chat.adapters.MyAdapter;
import com.stiffrock.chat.dto.MessageDTO;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemMessageNotification;
import com.stiffrock.chat.items.ItemMessageRecieved;
import com.stiffrock.chat.items.ItemMessageSent;
import com.stiffrock.chat.model.BaseChat;
import com.stiffrock.chat.model.CurrentUser;
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
import com.stiffrock.chat.utils.WebSocketNotificationListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//TODO: ORGANISE CONTACTS BY MOST RECENT CHAT in the contacts fragment
//TODO: LEAVE/JOIN GROUPCHAT AND NOTIF
//TODO PONER UN SCROLLVIEW PAR ACUYANOD SE HABRE EL TECLADO
public class ChatActivity extends AppCompatActivity implements WebSocketNotificationListener {
    private final List<Item> msgItems = new ArrayList<>();
    private final Set<Message> messages = new HashSet<>();

    private ImageView onlineStatus;
    private EditText etMensaje;

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    private ApiService apiService;

    private WebSocketClient wsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initToolbarMenu();

        apiService = RetrofitClient.getApiService();

        etMensaje = findViewById(R.id.etMensaje);

        findViewById(R.id.sendText).setOnClickListener(e -> sendMessage());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyAdapter(msgItems);

        getMessageHistory();

        recyclerView.setAdapter(adapter);

        wsClient = WebSocketClient.getInstance();

        if (CurrentUser.getCurrentChat() instanceof GroupChat) {
            wsClient.connectToGroupChat();
        }

        wsClient.setOnNotificationReceivedListener(this);
    }

    private void initToolbarMenu() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);


//        TODO: PFP
//        ImageView ivContactPhoto = toolbar.findViewById(R.id.ivContactPhoto);
//        ivContactPhoto.setImageResource();

        TextView tvContactName = toolbar.findViewById(R.id.tvContactName);
        tvContactName.setText(getChatName(CurrentUser.getCurrentChat()));

        onlineStatus = toolbar.findViewById(R.id.ivOnlineStatus);

        toolbar.findViewById(R.id.contactContainter).setOnClickListener(e -> {
            Toast.makeText(ChatActivity.this, "IMPLEMENT", Toast.LENGTH_SHORT).show();
        });

        toolbar.findViewById(R.id.btnHome).setOnClickListener(e -> navigateToHomeActivity());

        CardView contactContainer = toolbar.findViewById(R.id.contactContainter);

        contactContainer.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(
                    this,
                    R.anim.scale_down
            ));
            v.postDelayed(() -> {
                v.startAnimation(AnimationUtils.loadAnimation(
                        this,
                        R.anim.scale_up
                ));
            }, 100);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        wsGetContactsStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (CurrentUser.getCurrentChat() instanceof GroupChat) {
            wsClient.disconnectFromGroupChat();
        }
    }

    private void wsGetContactsStatus() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", WebSocketAction.GET_CONTACTS_ONLINE_STATUS.name());
        String currentUserJson = GsonManager.gson.toJson(CurrentUser.getCurrentUser());
        jsonObject.add("content", GsonManager.gson.fromJson(currentUserJson, JsonObject.class));
        wsClient.sendMessage(jsonObject.toString());
    }

    private void getMessageHistory() {
        Long chatId = CurrentUser.getCurrentChat().getId();
        Call<List<Message>> call = apiService.getMessageHistory(chatId);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Message> msgHistory = response.body();
                    for (Message msg : msgHistory) {
                        if (!messages.contains(msg)) {
                            messages.add(msg);
                            User sender = msg.getSender();
                            int type = sender.equals(CurrentUser.getCurrentUser()) ? 1 : 2;
                            addTextBubble(type, sender.getUsername(), msg.getMessageContent(), formatDateTime(msg.getTimestamp()));
                        }
                    }
                } else {
                    Log.e(TAG, "Error loading message history");
                    Toast.makeText(ChatActivity.this, "Error cargando historial de mensajes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable throwable) {
                Log.e(TAG, "GetMessageHistory request failed: " + throwable.getMessage());
                Toast.makeText(ChatActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addTextBubble(int itemType, String sender, String text, String timestamp) {
        if (CurrentUser.getCurrentChat() instanceof PrivateChat) sender = "";

        if (itemType == 1) {
            msgItems.add(new ItemMessageSent(sender, text, timestamp));
        } else if (itemType == 2) {
            msgItems.add(new ItemMessageRecieved(sender, text, timestamp));
        } else if (itemType == 4) {
            msgItems.add(new ItemMessageNotification(text));
        }

        adapter.notifyItemInserted(msgItems.size() - 1);
        recyclerView.scrollToPosition(msgItems.size() - 1);
    }

    private void sendMessage() {
        String text = etMensaje.getText().toString().trim();
        if (text.isBlank()) return;
        User user = CurrentUser.getCurrentUser();
        etMensaje.setText("");
        addTextBubble(1, user.getUsername(), text, formatDateTime(LocalDateTime.now()));
        Long userId = user.getId();
        Long chatID = CurrentUser.getCurrentChat().getId();
        apiSendMessage(new MessageDTO(userId, chatID, text));
    }

    private String formatDateTime(LocalDateTime now) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return now.format(formatter);
    }

    private void recieveMessage(Message msg) {
        messages.add(msg);

        BaseChat currentChat = CurrentUser.getCurrentChat();
        BaseChat msgChat = msg.getChat();

        Long chatId = msgChat.getId();
        Long currentChatId = currentChat.getId();

        String sender = msg.getSender().getUsername();

        int itemType = 2;
        if (sender.equals("SYSTEM")) itemType = 4;

        if (chatId.equals(currentChatId)) {
            addTextBubble(itemType, sender, msg.getMessageContent(), formatDateTime(msg.getTimestamp()));
        } else showNotification(msg);
    }

    //TODO: HANDLE FALIED CONNECTIONS
    private void apiSendMessage(MessageDTO messageDTO) {
        Call<Message> call = apiService.sendMessage(messageDTO);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Message msg = response.body();
                    messages.add(msg);
                } else {
                    Log.e(TAG, "Error sending message: " + response.code());
                    Toast.makeText(ChatActivity.this, "Error enviado el mensaje", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Log.e(TAG, "SendMesssage request failed: " + t.getMessage());
                Toast.makeText(ChatActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //TODO: TEST
    private void showNotification(Message msg) {
        BaseChat chat = msg.getChat();
        Toast.makeText(this, "Mensaje recibido de " + getChatName(chat), Toast.LENGTH_SHORT).show();
    }

    private String getChatName(BaseChat chat) {
        String name;
        if (chat instanceof PrivateChat) name = ((PrivateChat) chat).getName();
        else name = ((GroupChat) chat).getName();
        String[] usernames = name.split("&");
        String currentUsrName = CurrentUser.getCurrentUser().getUsername();
        return usernames[0].equals(currentUsrName) ? usernames[1] : usernames[0];
    }

    //TODO: QUIZAS ESTO TAMBIEN EN EL CONTANCTFRAGMENT CON LA NOTIFICACION DE TOAST
    @Override
    public void onNotificationReceived(String notification) {
        WebSocketNotification wsn = WebSocketNotification.parse(notification);
        switch (wsn.getAction()) {
            case MESSAGE_RECEIVED:
            case USER_CONNECTED_TO_GROUP_CHAT:
            case USER_DISCONNECTED_FROM_GROUP_CHAT:
                Message msg = (Message) wsn.getContent();
                recieveMessage(msg);
                break;
            case USER_CONNECTED:
                onlineStatus.setImageResource(R.drawable.connected_icon);
                break;
            case USER_DISCONNECTED:
                onlineStatus.setImageResource(R.drawable.disconnected_icon);
                break;
            case MESSAGE_READ:
                // TODO
                break;
            case MESSAGE_DELETED:
                // TODO
                break;
            default:
                break;
        }
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}