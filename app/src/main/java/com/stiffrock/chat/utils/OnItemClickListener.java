package com.stiffrock.chat.utils;

import android.view.View;

import com.stiffrock.chat.items.Item;

/**
 * Interfaz para gestionar los eventos de click en los elementos de un RecyclerView.
 */
public interface OnItemClickListener {

    /**
     * Método que se llama cuando un elemento en el RecyclerView es clickado.
     *
     * @param view     La vista que fue clickada.
     * @param item     El elemento de tipo Item asociado a la vista clickada.
     * @param position La posición del elemento en el RecyclerView.
     */
    void onItemClick(View view, Item item, int position);

    /**
     * Método que se llama cuando un elemento en el RecyclerView es clickado de forma prolongada (selección).
     *
     * @param view     La vista que fue clickada.
     * @param item     El elemento de tipo Item asociado a la vista clickada.
     * @param position La posición del elemento en el RecyclerView.
     */
    void onLongItemClick(View view, Item item, int position);
}
