package com.stiffrock.chat.utils;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.stiffrock.chat.dto.CredentialsDTO;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Clase para gestionar el almacenamiento seguro de las credenciales de usuario utilizando SharedPreferences encriptadas.
 * Este almacenamiento se utiliza cuando el usuario selecciona la opción "Recordarme" en el login.
 */
public class SecureStorage {

    private static final String PREFS_NAME = "user_secure_prefs";
    private static final String USERNAME_KEY = "hashed_username";
    private static final String PASSWORD_KEY = "hashed_password";

    /**
     * Obtiene las SharedPreferences encriptadas utilizando una clave maestra.
     *
     * @param context El contexto de la aplicación.
     * @return Las SharedPreferences encriptadas.
     * @throws GeneralSecurityException Si ocurre un error con la encriptación.
     * @throws IOException              Si ocurre un error al leer las preferencias.
     */
    private SharedPreferences getEncryptedPrefs(Context context) throws GeneralSecurityException, IOException {
        MasterKey masterKey = new MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();

        return EncryptedSharedPreferences.create(context, PREFS_NAME, masterKey, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
    }

    /**
     * Guarda las credenciales de usuario de manera segura en SharedPreferences encriptadas.
     *
     * @param context        El contexto de la aplicación.
     * @param credentialsDTO El objeto {@link CredentialsDTO} que contiene las credenciales del usuario.
     */
    public void saveUserCredentials(Context context, CredentialsDTO credentialsDTO) {
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            prefs.edit().putString(USERNAME_KEY, credentialsDTO.getUsername()).apply();
            prefs.edit().putString(PASSWORD_KEY, credentialsDTO.getPassword()).apply();
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Error saving to EncryptedPrefs: " + e.getMessage());
        }
    }

    /**
     * Recupera las credenciales de usuario desde SharedPreferences encriptadas.
     *
     * @param context El contexto de la aplicación.
     * @return Un objeto {@link CredentialsDTO} con las credenciales almacenadas, o null si ocurre un error.
     */
    public CredentialsDTO getUserCredentials(Context context) {
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            String usr = prefs.getString(USERNAME_KEY, null);
            String pwd = prefs.getString(PASSWORD_KEY, null);
            return new CredentialsDTO(usr, pwd);
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Error getting from EncryptedPrefs: " + e.getMessage());
            return null;
        }
    }
}
