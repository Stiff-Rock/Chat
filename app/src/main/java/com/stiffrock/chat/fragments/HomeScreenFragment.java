package com.stiffrock.chat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stiffrock.chat.MainActivity;
import com.stiffrock.chat.R;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemChatCard;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.model.MyAdapter;
import com.stiffrock.chat.utils.OnChatCardClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeScreenFragment extends Fragment implements OnChatCardClickListener {
    private List<Item> chatList;

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        chatList = new ArrayList<>();

        List<String> participants = Arrays.asList("yago", "yogo");
        if (CurrentUser.getUsername().equals("yago"))
            chatList.add(new ItemChatCard("yogo", participants, false, "1234"));
        else if (CurrentUser.getUsername().equals("yogo"))
            chatList.add(new ItemChatCard("yago", participants, false, "1234"));

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new MyAdapter(chatList, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onChatCardClick(ItemChatCard chat) {
        ((MainActivity) requireActivity()).replaceFragment(new ChatFragment(chat.getChatName()));
    }
}