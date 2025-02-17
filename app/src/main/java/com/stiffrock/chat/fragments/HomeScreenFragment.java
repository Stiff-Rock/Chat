package com.stiffrock.chat.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stiffrock.chat.MainActivity;
import com.stiffrock.chat.R;
import com.stiffrock.chat.items.ItemChatCard;
import com.stiffrock.chat.model.MyAdapter;
import com.stiffrock.chat.utils.OnChatCardClickListener;

import java.util.ArrayList;

public class HomeScreenFragment extends Fragment implements OnChatCardClickListener {
    private MyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new MyAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void showAddContactDialog(Context context) {
        EditText editText = new EditText(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Text")
                .setMessage("Please input some text:")
                .setView(editText)
                .setPositiveButton("OK", (dialog, which) -> {
                    String inputText = editText.getText().toString().trim();

                    addContact(inputText);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    private void addContact(String input) {
        Toast.makeText(requireContext(), "User input: " + input, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChatCardClick(ItemChatCard chat) {
        ((MainActivity) requireActivity()).replaceFragment(new ChatFragment(chat.getChatName()));
    }
}