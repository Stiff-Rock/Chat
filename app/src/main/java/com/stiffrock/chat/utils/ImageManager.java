package com.stiffrock.chat.utils;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import com.stiffrock.chat.R;
import com.stiffrock.chat.dto.UploadResponse;
import com.stiffrock.chat.network.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            String mimeType = activity.getContentResolver().getType(uri);
            if (!"image/jpeg".equals(mimeType) && !"image/png".equals(mimeType)) {
                Toast.makeText(activity, "Formato no permitido. Usa JPG o PNG", Toast.LENGTH_SHORT).show();
                return false;
            }

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

    public static void apiUploadImage(Context context, Uri imageUri, UploadCallback uploadCallback) {
        File file = new File(getRealPathFromURI(context, imageUri));
        RequestBody requestBody = RequestBody.create(file, MediaType.parse("image/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        Call<UploadResponse> call = RetrofitClient.getApiService().uploadImage(part);
        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(@NonNull Call<UploadResponse> call, @NonNull Response<UploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UploadResponse fotoUrl = response.body();
                    uploadCallback.onUploaded(fotoUrl.getUrl());
                } else {
                    Log.e(TAG, "Upload image failed: " + response.code());
                    Toast.makeText(context, "Error subiendo foto al servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UploadResponse> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Upload image failed: " + throwable.getMessage());
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static String getRealPathFromURI(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else {
            return uri.getPath();
        }
    }

    public static void setImageViewPhoto(Context context, ImageView imageView, String url) {
        GlideApp.with(context).load(url).placeholder(R.drawable.loading_dots).error(R.drawable.default_user).into(imageView);
    }

    public interface UploadCallback {
        void onUploaded(String fotoUrl);
    }
}