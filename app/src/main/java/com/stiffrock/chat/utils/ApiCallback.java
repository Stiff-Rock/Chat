package com.stiffrock.chat.utils;

public interface ApiCallback<T> {
    void onResult(boolean success, T result);
}
