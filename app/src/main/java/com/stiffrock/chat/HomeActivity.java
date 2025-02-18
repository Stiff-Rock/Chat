package com.stiffrock.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemChatCard;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.MyAdapter;
import com.stiffrock.chat.model.WebSocketClient;
import com.stiffrock.chat.utils.OnChatCardClickListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements OnChatCardClickListener {
    private MyAdapter adapter;
    private List<Item> chatCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //TODO: LOAD CONTACT CLASS EITHER FROM LOCAL STORAGE OR REMOTE
        chatCards = new ArrayList<>();
        adapter = new MyAdapter(chatCards, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addChat) {
            showAddContactDialog();
            return true;
        } else if (item.getItemId() == R.id.logOut) {
            logOut();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void logOut() {
        CurrentUser.setUsername("");
        WebSocketClient.getInstance().disconnect();

        SharedPreferences sp = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        sp.edit().putBoolean("rememberLogIn", false).apply();
        sp.edit().putString("storedUser", "").apply();

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        navigateToActivity(MainActivity.class, null);
    }

    public void showAddContactDialog() {
        EditText editText = new EditText(this);

        //TODO: BUSQUEDA POR USERNAME O POR ID
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Text").setMessage("Please input some text:").setView(editText).setPositiveButton("OK", (dialog, which) -> {
            String inputText = editText.getText().toString().trim();

            if (!inputText.isBlank()) addContact(inputText);
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    //TODO: ESTO ESTA FATAL
    private void addContact(String input) {
        chatCards.add(new ItemChatCard(input, new ArrayList<>(), false));
        adapter.notifyItemInserted(chatCards.size() - 1);
    }

    @Override
    public void onChatCardClick(ItemChatCard chat) {
        Bundle bundle = new Bundle();
        bundle.putString("recipient", chat.getChatName());
        navigateToActivity(ChatActivity.class, bundle);
    }

    private void navigateToActivity(Class<?> targetActivity, Bundle bundle) {
        Intent intent = new Intent(HomeActivity.this, targetActivity);
        if (bundle != null) intent.putExtras(bundle);
        startActivity(intent);
    }
}