package com.stiffrock.chat.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stiffrock.chat.R;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemChatCard;
import com.stiffrock.chat.items.ItemMessageRecieved;
import com.stiffrock.chat.items.ItemMessageSent;
import com.stiffrock.chat.utils.OnChatCardClickListener;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Item> datos;

    private OnChatCardClickListener listener;

    public MyAdapter(List<Item> datos) {
        this.datos = datos;
    }

    public MyAdapter(List<Item> datos, OnChatCardClickListener listener) {
        this.datos = datos;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return datos.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_recieved, parent, false);
            return new ViewHolderMessageRecieved(view);
        } else if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new ViewHolderMessageSent(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_card, parent, false);
            return new ViewHolderChatCard(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderMessageRecieved) {
            ViewHolderMessageRecieved view = (ViewHolderMessageRecieved) holder;
            ItemMessageRecieved item = (ItemMessageRecieved) datos.get(position);

            view.textView.setText(item.getMessage());
        } else if (holder instanceof ViewHolderMessageSent) {
            ViewHolderMessageSent view = (ViewHolderMessageSent) holder;
            ItemMessageSent item = (ItemMessageSent) datos.get(position);

            view.textView.setText(item.getMessage());
        } else {
            ViewHolderChatCard view = (ViewHolderChatCard) holder;
            ItemChatCard item = (ItemChatCard) datos.get(position);

            view.parentLayout.setOnClickListener(e -> listener.onChatCardClick(item));
            view.ivChatPhoto.setImageResource(R.drawable.default_user);
            view.tvChatName.setText(item.getChatName());
        }
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public static class ViewHolderMessageRecieved extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolderMessageRecieved(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_recieved);
        }
    }

    public static class ViewHolderMessageSent extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolderMessageSent(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_sent);
        }
    }

    public static class ViewHolderChatCard extends RecyclerView.ViewHolder {
        private final LinearLayout parentLayout;
        private final ImageView ivChatPhoto;
        private final TextView tvChatName;

        public ViewHolderChatCard(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            ivChatPhoto = itemView.findViewById(R.id.ivChatPhoto);
            tvChatName = itemView.findViewById(R.id.tvChatName);
        }
    }
}