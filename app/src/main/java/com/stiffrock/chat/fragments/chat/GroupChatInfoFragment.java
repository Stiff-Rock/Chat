package com.stiffrock.chat.fragments.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.stiffrock.chat.R;
import com.stiffrock.chat.model.GroupChat;

public class GroupChatInfoFragment extends Fragment {
    private GroupChat chat;

    public GroupChatInfoFragment(GroupChat chat) {
        this.chat = chat;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat_info, container, false);
        return view;
    }
}