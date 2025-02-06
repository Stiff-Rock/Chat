package com.stiffrock.chat.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stiffrock.chat.R;
import com.stiffrock.chat.items.Item;
import com.stiffrock.chat.items.ItemMessageRecieved;
import com.stiffrock.chat.items.ItemMessageSent;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Item> datos;

    public MyAdapter(List<Item> datos) {
        this.datos = datos;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_message_recieved, parent, false);
            return new ViewHolderMessageRecieved(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_message_sent, parent, false);
            return new ViewHolderMessageSent(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderMessageRecieved) {
            ViewHolderMessageRecieved vmr = (ViewHolderMessageRecieved) holder;
            ItemMessageRecieved item = (ItemMessageRecieved) datos.get(position);

            vmr.textView.setText(item.getMessage());
        } else {
            ViewHolderMessageSent vmr = (ViewHolderMessageSent) holder;
            ItemMessageSent item = (ItemMessageSent) datos.get(position);

            vmr.textView.setText(item.getMessage());
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
}