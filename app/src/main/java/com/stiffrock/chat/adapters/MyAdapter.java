package com.stiffrock.chat.adapters;

import android.graphics.Color;
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
import com.stiffrock.chat.items.ItemContactCard;
import com.stiffrock.chat.items.ItemMessageNotification;
import com.stiffrock.chat.items.ItemMessageRecieved;
import com.stiffrock.chat.items.ItemMessageSent;
import com.stiffrock.chat.model.BaseChat;
import com.stiffrock.chat.model.GroupChat;
import com.stiffrock.chat.model.PrivateChat;
import com.stiffrock.chat.utils.OnItemClickListener;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Item> datos;

    private OnItemClickListener listener;

    public MyAdapter(List<Item> datos) {
        this.datos = datos;
    }

    public MyAdapter(List<Item> datos, OnItemClickListener listener) {
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
        } else if (viewType == 2) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_card, parent, false);
            return new ViewHolderChatCard(view);
        } else if (viewType == 3) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_card, parent, false);
            return new ViewHolderContactCard(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_notification, parent, false);
            return new ViewHolderMessageNotification(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderMessageRecieved) {
            ViewHolderMessageRecieved view = (ViewHolderMessageRecieved) holder;
            ItemMessageRecieved item = (ItemMessageRecieved) datos.get(position);

            String sender = item.getSender();
            if (sender.isBlank()) view.sender.setVisibility(View.GONE);
            else view.sender.setText(sender);

            view.textRecieved.setText(item.getMessage());
            view.timeStamp.setText(item.getTimestamp());
        } else if (holder instanceof ViewHolderMessageSent) {
            ViewHolderMessageSent view = (ViewHolderMessageSent) holder;
            ItemMessageSent item = (ItemMessageSent) datos.get(position);

            String sender = item.getSender();
            if (sender.isBlank()) view.sender.setVisibility(View.GONE);
            else view.sender.setText(sender);

            view.textSent.setText(item.getMessage());
            view.timeStamp.setText(item.getTimestamp());
        } else if (holder instanceof ViewHolderChatCard) {
            ViewHolderChatCard view = (ViewHolderChatCard) holder;
            ItemChatCard item = (ItemChatCard) datos.get(position);

            view.parentLayout.setOnClickListener(e -> listener.onItemClick(item));

            BaseChat chat = item.getChat();
            if (chat instanceof GroupChat) {
                view.ivChatPhoto.setImageResource(R.drawable.default_group);
                view.ivOnlineStatus.setVisibility(View.GONE);
            } else if (chat instanceof PrivateChat) {
                //TODO PFPs
                view.ivChatPhoto.setImageResource(R.drawable.default_user);
                int imgSrc = item.isOnline() ? R.drawable.connected_icon : R.drawable.disconnected_icon;
                view.ivOnlineStatus.setImageResource(imgSrc);
            }

            view.tvChatName.setText(item.getChatName());
        } else if (holder instanceof ViewHolderContactCard) {
            ViewHolderContactCard view = (ViewHolderContactCard) holder;
            ItemContactCard item = (ItemContactCard) datos.get(position);

            view.parentLayout.setOnClickListener(e -> listener.onItemClick(item));

            if (!item.showOnlineStatus()) view.ivUserStatus.setVisibility(View.GONE);

            if (item.isSelected()) {
                view.ivSelected.setVisibility(View.VISIBLE);
                view.parentLayout.setBackgroundColor(Color.parseColor("#1E1E2E"));
            } else {
                view.ivSelected.setVisibility(View.GONE);
                view.parentLayout.setBackgroundColor(Color.TRANSPARENT);
            }

            //TODO PFPs
            view.ivContactPhoto.setImageResource(R.drawable.default_user);
            view.tvContactName.setText(item.getName());
        } else {
            ViewHolderMessageNotification view = (ViewHolderMessageNotification) holder;
            ItemMessageNotification item = (ItemMessageNotification) datos.get(position);

            view.textNotification.setText(item.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public static class ViewHolderMessageRecieved extends RecyclerView.ViewHolder {
        private final TextView sender;
        private final TextView textRecieved;
        private final TextView timeStamp;

        public ViewHolderMessageRecieved(@NonNull View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.sender);
            textRecieved = itemView.findViewById(R.id.textRecieved);
            timeStamp = itemView.findViewById(R.id.timeStamp);
        }
    }

    public static class ViewHolderMessageSent extends RecyclerView.ViewHolder {
        private final TextView sender;
        private final TextView textSent;
        private final TextView timeStamp;

        public ViewHolderMessageSent(@NonNull View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.sender);
            textSent = itemView.findViewById(R.id.textSent);
            timeStamp = itemView.findViewById(R.id.timeStamp);
        }
    }

    public static class ViewHolderMessageNotification extends RecyclerView.ViewHolder {
        private final TextView textNotification;

        public ViewHolderMessageNotification(@NonNull View itemView) {
            super(itemView);
            textNotification = itemView.findViewById(R.id.textNotification);
        }
    }

    public static class ViewHolderChatCard extends RecyclerView.ViewHolder {
        private final LinearLayout parentLayout;
        private final ImageView ivChatPhoto;
        private final ImageView ivOnlineStatus;
        private final TextView tvChatName;

        public ViewHolderChatCard(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            ivChatPhoto = itemView.findViewById(R.id.ivChatPhoto);
            ivOnlineStatus = itemView.findViewById(R.id.ivOnlineStatus);
            tvChatName = itemView.findViewById(R.id.tvChatName);
        }
    }

    public static class ViewHolderContactCard extends RecyclerView.ViewHolder {
        private final LinearLayout parentLayout;
        private final ImageView ivContactPhoto;
        private final ImageView ivUserStatus;
        private final ImageView ivSelected;
        private final TextView tvContactName;

        public ViewHolderContactCard(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            ivContactPhoto = itemView.findViewById(R.id.ivContactPhoto);
            ivUserStatus = itemView.findViewById(R.id.ivUserStatus);
            ivSelected = itemView.findViewById(R.id.ivSelected);
            tvContactName = itemView.findViewById(R.id.tvContactName);
        }
    }
}