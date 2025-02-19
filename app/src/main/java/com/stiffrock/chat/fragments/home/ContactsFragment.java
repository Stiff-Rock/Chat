package com.stiffrock.chat.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stiffrock.chat.ChatActivity;
import com.stiffrock.chat.HomeActivity;
import com.stiffrock.chat.R;
import com.stiffrock.chat.adapters.MyAdapter;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemChatCard;
import com.stiffrock.chat.model.CurrentUser;
import com.stiffrock.chat.utils.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class ContactsFragment extends Fragment implements OnItemClickListener {
    private MyAdapter adapter;

    private final List<Item> chats = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        CurrentUser.setCurrentChat(null);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new MyAdapter(chats, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(ItemChatCard chat) {
        CurrentUser.setCurrentChat(chat.getChat());
        ((HomeActivity) requireActivity()).navigateToActivity(ChatActivity.class);
    }
}