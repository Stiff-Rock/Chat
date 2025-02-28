package com.stiffrock.chat.utils;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageManager {
    public static final int MAX_IMAGE_SIZE_MB = 10;

    // Abre la galería seleccionada por el usuario
    public static void openGallery(ActivityResultLauncher<Intent> launcher) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        launcher.launch(intent);
    }


    // Asegura que la imagen tenga un formato válido y cumpla el peso máximo
    public static boolean isValidImage(Activity activity, Uri uri) {
        try {
            // Valida el formato de la imagen
            String mimeType = activity.getContentResolver().getType(uri);
            if (!"image/jpeg".equals(mimeType) && !"image/png".equals(mimeType)) {
                Toast.makeText(activity, "Formato no permitido. Usa JPG o PNG", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Valida el tamaño de la imagen
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);
            if (inputStream == null) throw new IOException("InputStream is null");
            int fileSize = inputStream.available();
            inputStream.close();

            if (fileSize > MAX_IMAGE_SIZE_MB * 1024 * 1024) {
                Toast.makeText(activity, "La imagen es demasiado grande (Máx 10MB)", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error loading image: " + e.getMessage());
            Toast.makeText(activity, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // Convierte el URI a un array de bytes
    public static byte[] getBytesFromUri(Activity activity, Uri uri) {
        try (InputStream is = activity.getContentResolver().openInputStream(uri);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            if (is == null) throw new IOException("InputStream is null");
            byte[] data = new byte[16384];
            int nRead;
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, "Error getting bytes from URI: " + e.getMessage());
            return null;
        }
    }
}
