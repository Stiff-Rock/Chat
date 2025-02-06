package com.stiffrock.chat;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemMessageRecieved;
import com.stiffrock.chat.items.ItemMessageSent;
import com.stiffrock.chat.model.MyAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private final List<Item> messagesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private EditText editText1;
    private EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editText1 = findViewById(R.id.editText1);

        findViewById(R.id.sendText1).setOnClickListener(e -> addTextBubble(editText1, 1));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyAdapter(messagesList);
        recyclerView.setAdapter(adapter);
    }

    private void addTextBubble(EditText eT, int itemType) {
        String text = eT.getText().toString().trim();
        if (!text.isBlank()) {
            if (itemType == 1) {
                messagesList.add(new ItemMessageSent(text));
            } else if (itemType == 2) {
                messagesList.add(new ItemMessageRecieved(text));
            }

            adapter.notifyItemInserted(messagesList.size() - 1);
            recyclerView.scrollToPosition(messagesList.size() - 1);

            eT.setText("");
        }
    }
}