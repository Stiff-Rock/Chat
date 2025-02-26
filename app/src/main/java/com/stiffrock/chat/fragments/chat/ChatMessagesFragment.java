package com.stiffrock.chat.fragments.chat;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stiffrock.chat.ChatActivity;
import com.stiffrock.chat.R;
import com.stiffrock.chat.adapters.MyAdapter;
import com.stiffrock.chat.dto.ApiResponse;
import com.stiffrock.chat.dto.MessageUpdateDto;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemMessageRecieved;
import com.stiffrock.chat.items.ItemMessageSent;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.Message;
import com.stiffrock.chat.model.MessageState;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.utils.OnItemClickListener;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatMessagesFragment extends Fragment implements OnItemClickListener {
    private RecyclerView recyclerView;
    private Map<Item, Message> itemMessageMap;

    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_messages, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                checkVisibleMessages();
            }
        });

        EditText etMensaje = view.findViewById(R.id.etMensaje);
        view.findViewById(R.id.sendText).setOnClickListener(e -> ((ChatActivity) requireActivity()).sendMessage(etMensaje));
        apiService = RetrofitClient.getApiService();
        return view;
    }

    private void apiDeleteMessage(Long msgId) {
        Call<ApiResponse> call = apiService.deleteMessage(msgId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (!response.isSuccessful() && response.body() == null) {
                    Log.e(TAG, "Error deleting message");
                    Toast.makeText(requireContext(), "Error borrando el mensaje", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.e(TAG, "DeleteMessage request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkVisibleMessages() {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int firstVisiblePos = linearLayoutManager.findFirstVisibleItemPosition();
            int lastVisiblePos = linearLayoutManager.findLastVisibleItemPosition();
            for (int i = firstVisiblePos; i <= lastVisiblePos; i++) {
                View itemView = linearLayoutManager.findViewByPosition(i);
                if (itemView == null) continue;

                Object tag = itemView.getTag();

                if (!(tag instanceof ItemMessageRecieved)) continue;

                ItemMessageRecieved imr = (ItemMessageRecieved) tag;

                Message msg = itemMessageMap.get(imr);
                User currentUser = CurrentUser.getCurrentUser();
                if (msg == null || msg.isDeleted() || msg.getSender().equals(currentUser)) continue;

                if (msg.getMessageState() != MessageState.READ || !msg.getReadBy().contains(currentUser)) {
                    MessageState state = msg.getMessageState();
                    MessageUpdateDto mud = new MessageUpdateDto(currentUser, msg, state);
                    sendMessageSatusChange(mud);
                }
            }
        }
    }

    private void sendMessageSatusChange(MessageUpdateDto mud) {
        Call<ApiResponse> call = apiService.updateMessageStatus(mud);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (!response.isSuccessful() && response.body() == null) {
                    Log.e(TAG, "Error updating message status: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.e(TAG, "SendMessageSatusChange request failed: " + throwable.getMessage());
            }
        });
    }

    private void openMsgPopup(View view, ItemMessageSent item) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.chat_context_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.deleteMsg) {
                Long id = Objects.requireNonNull(itemMessageMap.get(item)).getId();
                apiDeleteMessage(id);
            }
            return true;
        });

        popupMenu.show();
    }

    public MyAdapter initAdapter(List<Item> msgItems) {
        return new MyAdapter(msgItems, this);
    }

    public void setMap(Map<Item, Message> itemMessageMap) {
        this.itemMessageMap = itemMessageMap;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public void onItemClick(View view, Item item, int postion) {
    }

    @Override
    public void onLongItemClick(View view, Item item, int postion) {
        if (item instanceof ItemMessageSent) {
            ItemMessageSent ims = (ItemMessageSent) item;
            openMsgPopup(view, ims);
        }
    }
}