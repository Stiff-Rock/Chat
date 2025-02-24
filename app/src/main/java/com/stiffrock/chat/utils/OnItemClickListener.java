package com.stiffrock.chat.utils;

import android.view.View;

import com.stiffrock.chat.items.Item;

public interface OnItemClickListener {
    void onItemClick(View view, Item item);
    void onLongItemClick(View view,Item item);
}