package com.stiffrock.chat.fragments.home;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stiffrock.chat.HomeActivity;
import com.stiffrock.chat.R;
import com.stiffrock.chat.adapters.MyAdapter;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemContactCard;
import com.stiffrock.chat.utils.CurrentUser;
import com.stiffrock.chat.model.User;
import com.stiffrock.chat.network.ApiService;
import com.stiffrock.chat.network.RetrofitClient;
import com.stiffrock.chat.utils.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Clase del fragment que muestra la lista de usuarios conectados a la aplicación.
 * (Conectados al WebSocket). Permite añadir nuevos contactos.
 */
public class OnlineUsersFragment extends Fragment implements OnItemClickListener {
    private List<Item> contactCards;
    private List<User> users;

    private LinearLayout loadingScreen;

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online_users, container, false);

        apiService = RetrofitClient.getApiService();

        loadingScreen = view.findViewById(R.id.loadingScreen);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        apiGetOnlineUsers();

        return view;
    }

    /**
     * Envia un request al servidor para recibir de vuelta una lista de usuarios conectados a la app
     * y los carga en el RecyclerView.
     */
    private void apiGetOnlineUsers() {
        contactCards = new ArrayList<>();

        Call<List<User>> call = apiService.getOnlineUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    users = response.body();
                    for (User usr : users) {
                        if (!usr.equals(CurrentUser.getCurrentUser())) {
                            contactCards.add(new ItemContactCard(usr, true, false));
                        }
                    }
                    adapter = new MyAdapter(contactCards, OnlineUsersFragment.this);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    loadingScreen.setVisibility(View.GONE);
                    if (contactCards.isEmpty()) {
                        Toast.makeText(requireContext(), "No hay usuarios en línea actualmente", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadingScreen.setVisibility(View.GONE);
                    Log.e(TAG, "Error while getting online users list");
                    Toast.makeText(requireContext(), "Error añadiendo contacto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable throwable) {
                loadingScreen.setVisibility(View.GONE);
                Log.e(TAG, "GetOnlineUsers request failed: " + throwable.getMessage());
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Muestr aun popup al seleccionar un usuario para dar a elegir si se quiere añadir como contacto
     *
     * @param icc   Item del RecyclerView presionado
     * @param index Índice del item presionado en el RecyclerView
     * @param user  Usuario con el que el item presionado esta asociado
     */
    private void addContactDialog(ItemContactCard icc, int index, User user) {
        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = 30;
        layout.setPadding(padding, padding, padding, padding);

        int textColor = context.getColor(R.color.standard_text_color_tertiary);

        SpannableString title = new SpannableString("¿Desea añadir como contacto al usuario seleccionado?");
        title.setSpan(new ForegroundColorSpan(textColor), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setTitle(title).setView(layout).setPositiveButton("Aceptar", (dialog, which) -> {
            HomeActivity home = (HomeActivity) requireActivity();
            home.apiAddContact(user);
            icc.setSelected(false);
            adapter.notifyItemChanged(index);
        }).setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();
            icc.setSelected(false);
            adapter.notifyItemChanged(index);
        });

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

    // Listener para gestionar el item del RecyclerView que se ha clickado
    @Override
    public void onItemClick(View view, Item item, int postion) {
        ItemContactCard icc = (ItemContactCard) item;
        User user = icc.getUser();
        icc.setSelected(true);
        int index = contactCards.indexOf(item);
        adapter.notifyItemChanged(index);
        addContactDialog(icc, index, user);
    }

    @Override
    public void onLongItemClick(View view, Item item, int postion) {
    }
}