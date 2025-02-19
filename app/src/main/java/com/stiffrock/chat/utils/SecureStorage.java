package com.stiffrock.chat.utils;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.stiffrock.chat.dto.LoginDTO;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SecureStorage {

    private static final String PREFS_NAME = "user_secure_prefs";
    private static final String USERNAME_KEY = "hashed_username";
    private static final String PASSWORD_KEY = "hashed_password";

    private SharedPreferences getEncryptedPrefs(Context context) throws GeneralSecurityException, IOException {
        MasterKey masterKey = new MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();

        SharedPreferences securePrefs = EncryptedSharedPreferences.create(context, PREFS_NAME, masterKey, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

        return securePrefs;
    }

    public void saveUserCredentials(Context context, LoginDTO loginDTO) {
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            prefs.edit().putString(USERNAME_KEY, loginDTO.getUsername()).apply();
            prefs.edit().putString(PASSWORD_KEY, loginDTO.getPassword()).apply();
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Error saving to EncryptedPrefs: " + e.getMessage());
        }
    }

    public LoginDTO getUserCredentials(Context context) {
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            String usr = prefs.getString(USERNAME_KEY, null);
            String pwd = prefs.getString(PASSWORD_KEY, null);
            return new LoginDTO(usr, pwd);
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Error getting from EncryptedPrefs: " + e.getMessage());
            return null;
        }
    }
}
