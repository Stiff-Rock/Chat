package com.stiffrock.chat;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.stiffrock.chat.adapters.MyAdapter;
import com.stiffrock.chat.dto.MessageDTO;
import com.stiffrock.chat.fragments.chat.ChatMessagesFragment;
import com.stiffrock.chat.fragments.chat.GroupChatInfoFragment;
import com.stiffrock.chat.fragments.chat.ContactInfoFragment;
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
import com.stiffrock.chat.utils.FragmentContainerActivity;
import com.stiffrock.chat.utils.GsonManager;
import com.stiffrock.chat.utils.ImageManager;
import com.stiffrock.chat.utils.WebSocketNotificationListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends FragmentContainerActivity implements WebSocketNotificationListener {
    private final List<Item> msgItems = new ArrayList<>();
    private final Map<Item, Message> itemMessageMap = new HashMap<>();
    private final Map<Message, Item> messageItemMap = new HashMap<>();
    private final Set<Message> messages = new HashSet<>();

    private ImageView onlineStatus;
    private ImageView btnSearch;
    private boolean isSearching;
    private int currentMatchIndex = -1;

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

        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentResumed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                if (!(f instanceof ChatMessagesFragment)) return;
                super.onFragmentResumed(fm, f);

                recyclerView = ((ChatMessagesFragment) f).getRecyclerView();
                adapter = ((ChatMessagesFragment) f).initAdapter(msgItems);
                ((ChatMessagesFragment) f).setMap(itemMessageMap);

                getMessageHistory();

                recyclerView.setAdapter(adapter);
            }
        }, true);

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fcv);
            int visibility = currentFragment instanceof ChatMessagesFragment ? View.VISIBLE : View.GONE;
            btnSearch.setVisibility(visibility);
        });

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

        LinearLayout contactContainter = toolbar.findViewById(R.id.contactContainter);
        contactContainter.setOnClickListener(e -> {
            // Animacion personalizada al pulsar el LinearLayout del contacto
            e.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_down));
            e.postDelayed(() -> e.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_up)), 100);

            // Abre la vista de informacion del contacto/grupo
            BaseChat chat = CurrentUser.getCurrentChat();
            if (chat instanceof PrivateChat) {
                User contact = ((PrivateChat) chat).getContact(CurrentUser.getCurrentUser());
                replaceFragment(new ContactInfoFragment(contact));
            } else if (chat instanceof GroupChat) {
                replaceFragment(new GroupChatInfoFragment());
            }
        });

        ConstraintLayout searchBarContainer = toolbar.findViewById(R.id.searchBarContainer);

        ImageView ivContactPhoto = toolbar.findViewById(R.id.ivContactPhoto);

        TextView tvContactName = toolbar.findViewById(R.id.tvContactName);
        tvContactName.setText(getChatName(CurrentUser.getCurrentChat()));

        onlineStatus = toolbar.findViewById(R.id.ivOnlineStatus);

        EditText etSearchText = toolbar.findViewById(R.id.etSearchText);
        etSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        toolbar.findViewById(R.id.btnUp).setOnClickListener(v -> {
            int totalMatches = adapter.getMatchesCount();
            if (totalMatches > 0) {
                currentMatchIndex = (currentMatchIndex - 1 + totalMatches) % totalMatches;
                int targetPosition = adapter.getMatchAt(currentMatchIndex);
                if (targetPosition != -1) {
                    recyclerView.smoothScrollToPosition(targetPosition);
                }
            }
        });

        toolbar.findViewById(R.id.btnDown).setOnClickListener(v -> {
            int totalMatches = adapter.getMatchesCount();
            if (totalMatches > 0) {
                currentMatchIndex = (currentMatchIndex + 1) % totalMatches;
                int targetPosition = adapter.getMatchAt(currentMatchIndex);
                if (targetPosition != -1) {
                    recyclerView.smoothScrollToPosition(targetPosition);
                }
            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm);

        isSearching = false;
        btnSearch = toolbar.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            isSearching = !isSearching;
            int color;
            if (isSearching) {
                color = getColor(R.color.md_theme_dark_background);
                contactContainter.setVisibility(View.GONE);
                searchBarContainer.setVisibility(View.VISIBLE);
                etSearchText.requestFocus();
                imm.showSoftInput(etSearchText, InputMethodManager.SHOW_IMPLICIT);
            } else {
                color = Color.TRANSPARENT;
                contactContainter.setVisibility(View.VISIBLE);
                searchBarContainer.setVisibility(View.GONE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            etSearchText.setText("");
            adapter.setSearchQuery("");
            btnSearch.setBackgroundColor(color);
        });

        BaseChat chat = CurrentUser.getCurrentChat();
        if (chat instanceof PrivateChat) {
            User contact = ((PrivateChat) chat).getContact(CurrentUser.getCurrentUser());
            onlineStatus.setVisibility(View.VISIBLE);
            if (contact.getProfilePictureUrl() != null) {
                ImageManager.setImageViewPhoto(this, ivContactPhoto, contact.getProfilePictureUrl(), null);
            } else {
                ivContactPhoto.setImageResource(R.drawable.default_user);
            }
        } else if (chat instanceof GroupChat) {
            onlineStatus.setVisibility(View.GONE);

            if (((GroupChat) chat).getChatPhotoUrl() != null) {
                ImageManager.setImageViewPhoto(this, ivContactPhoto, ((GroupChat) chat).getChatPhotoUrl(), null);
            } else {
                ivContactPhoto.setImageResource(R.drawable.default_group);
            }
        }

        toolbar.findViewById(R.id.btnHome).

                setOnClickListener(v ->

        {
            if (!isSearching) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fcv);
                if (fragment instanceof ChatMessagesFragment) navigateToHomeActivity();
                else replaceFragment(new ChatMessagesFragment());
            } else {
                contactContainter.setVisibility(View.VISIBLE);
                searchBarContainer.setVisibility(View.GONE);
                etSearchText.setText("");
                adapter.setSearchQuery("");
                btnSearch.setBackgroundColor(Color.TRANSPARENT);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
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
                            int type = sender.equals(CurrentUser.getCurrentUser()) ? 1 : sender.getUsername().equals("SYSTEM") ? 4 : 2;
                            addTextBubble(type, msg);
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

    public void addTextBubble(int itemType, Message msg) {
        String sender = msg.getSender().getUsername();
        String text = msg.getMessageContent();
        String timestamp = formatDateTime(msg.getTimestamp());

        if (CurrentUser.getCurrentChat() instanceof PrivateChat) sender = "";

        Item item;
        if (itemType == 1) {
            item = new ItemMessageSent(sender, text, timestamp, msg.getMessageState());
            msgItems.add(item);
        } else if (itemType == 2) {
            item = new ItemMessageRecieved(sender, text, timestamp);
            msgItems.add(item);
        } else if (itemType == 4) {
            item = new ItemMessageNotification(text);
            msgItems.add(item);
        } else return;

        itemMessageMap.put(item, msg);
        messageItemMap.put(msg, item);

        adapter.notifyItemInserted(msgItems.size() - 1);
        recyclerView.scrollToPosition(msgItems.size() - 1);
    }

    public void sendMessage(EditText etMensaje) {
        String text = etMensaje.getText().toString().trim();
        if (text.isBlank()) return;
        User user = CurrentUser.getCurrentUser();
        etMensaje.setText("");

        Message msg = new Message();
        msg.setSender(CurrentUser.getCurrentUser());
        msg.setMessageContent(text);
        msg.setTimestamp(LocalDateTime.now());
        addTextBubble(1, msg);

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
            addTextBubble(itemType, msg);
        } else showNotification(msg);
    }

    private void updateMessage(Message msg) {
        Item item = messageItemMap.get(msg);
        int index = msgItems.indexOf(item);

        if (item instanceof ItemMessageSent) {
            ((ItemMessageSent) item).setMessage(msg.getMessageContent());
            ((ItemMessageSent) item).setMessageState(msg.getMessageState());
        } else if (item instanceof ItemMessageRecieved) {
            ((ItemMessageRecieved) item).setMessage(msg.getMessageContent());
        }

        adapter.notifyItemChanged(index);
    }

    private void apiSendMessage(MessageDTO messageDTO) {
        Call<Message> call = apiService.sendMessage(messageDTO);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Message msg = response.body();
                    messages.add(msg);

                    Item item = messageItemMap.remove(new Message());
                    itemMessageMap.put(item, msg);
                    messageItemMap.put(msg, item);

                    updateMessage(msg);
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
            case MESSAGE_DELETED:
                Message deletedMsg = (Message) wsn.getContent();
                updateMessage(deletedMsg);
                break;
            case MESSAGE_READ:
                Message readMsg = (Message) wsn.getContent();
                if (readMsg.getSender().equals(CurrentUser.getCurrentUser())) {
                    updateMessage(readMsg);
                }
                break;
            case DELETE_GROUP:
            case DELETE_CONTACT:
                BaseChat deleteChat = (BaseChat) wsn.getContent();
                if (CurrentUser.getCurrentChat().equals(deleteChat)) {
                    String name = null;
                    if (deleteChat instanceof PrivateChat) {
                        name = ((PrivateChat) deleteChat).getName();
                    } else if (deleteChat instanceof GroupChat) {
                        name = ((GroupChat) deleteChat).getName();
                    }
                    Toast.makeText(this, "Ya no participas en el chat \"" + name + "\"", Toast.LENGTH_SHORT).show();
                    navigateToHomeActivity();
                }
                break;
            case USER_CONNECTED:
                onlineStatus.setImageResource(R.drawable.connected_icon);
                break;
            case USER_DISCONNECTED:
                onlineStatus.setImageResource(R.drawable.disconnected_icon);
                break;
            case GROUP_CHAT_CHANGED:
                Fragment fragmentGCC = getSupportFragmentManager().findFragmentById(R.id.fcv);
                if (fragmentGCC instanceof GroupChatInfoFragment) {
                    JsonObject content = (JsonObject) wsn.getContent();
                    JsonObject groupChatJson = content.getAsJsonObject("groupChat");
                    JsonObject userJson = content.getAsJsonObject("user");
                    GroupChat chat = GsonManager.gson.fromJson(groupChatJson, GroupChat.class);
                    User user = GsonManager.gson.fromJson(userJson, User.class);
                    ((GroupChatInfoFragment) fragmentGCC).updateChat(WebSocketAction.GROUP_CHAT_CHANGED, chat, user);
                }
                break;
            case GROUP_CHAT_DELETION:
                Fragment fragmentGCD = getSupportFragmentManager().findFragmentById(R.id.fcv);
                if (fragmentGCD instanceof GroupChatInfoFragment) {
                    JsonObject content = (JsonObject) wsn.getContent();
                    JsonObject groupChatJson = content.getAsJsonObject("groupChat");
                    JsonObject userJson = content.getAsJsonObject("user");
                    GroupChat chat = GsonManager.gson.fromJson(groupChatJson, GroupChat.class);
                    User user = GsonManager.gson.fromJson(userJson, User.class);
                    ((GroupChatInfoFragment) fragmentGCD).updateChat(WebSocketAction.GROUP_CHAT_DELETION, chat, user);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        super.replaceFragment(fragment);
        int visibility = fragment instanceof ChatMessagesFragment ? View.VISIBLE : View.GONE;
        btnSearch.setVisibility(visibility);
    }

    public void navigateToHomeActivity() {
        Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}