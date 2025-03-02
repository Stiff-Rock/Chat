package com.stiffrock.chat.utils;

import static com.stiffrock.chat.network.ServerConfig.SOCKET_ADDR;
import static com.stiffrock.chat.utils.LogTag.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.stiffrock.chat.R;
import com.stiffrock.chat.dto.UploadResponse;
import com.stiffrock.chat.network.RetrofitClient;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Clase utilitaria para la gestión de la validación de imagenes seleciconadas, su subida a la BBDD
 * y el establecimiento imagenes de ImageViews a partir de URLs usando la libreria de Glide.
 */
public class ImageManager {
    public static final int MAX_IMAGE_SIZE_MB = 10;

    /**
     * Abre la galería del dispositivo para que el usuario seleccione una imagen.
     * Si es necesario, solicita los permisos de acceso a los archivos.
     *
     * @param activity La actividad desde la que se invoca.
     * @param launcher El lanzador de resultados para manejar la selección de la imagen.
     */
    public static void openGallery(Activity activity, ActivityResultLauncher<Intent> launcher) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 100);
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                return;
            }
        }

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    /**
     * Valida si la imagen seleccionada cumple con los requisitos de formato y tamaño.
     *
     * @param activity La actividad desde la que se invoca.
     * @param uri      La URI de la imagen seleccionada.
     * @return true si la imagen es válida, false en caso contrario.
     */
    public static boolean isValidImage(Activity activity, Uri uri) {
        try {
            ContentResolver resolver = activity.getContentResolver();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream is = resolver.openInputStream(uri);
            if (is == null) return false;
            BitmapFactory.decodeStream(is, null, options);
            is.close();

            if (options.outWidth > 4096 || options.outHeight > 4096) {
                Toast.makeText(activity, "La imagen es demasiado grande", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Verificar tipo MIME
            String mimeType = resolver.getType(uri);

            if (mimeType == null) return false;

            if (!mimeType.startsWith("image/")) {
                Toast.makeText(activity, "Formato no permitido", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Verificar tamaño usando ContentResolver
            ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "r");
            if (pfd == null) return false;

            long size = new ParcelFileDescriptor.AutoCloseInputStream(pfd).available();
            pfd.close();

            if (size > MAX_IMAGE_SIZE_MB * 1024 * 1024) {
                Toast.makeText(activity, "La imagen es demasiado grande", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error validando imagen: " + e.getMessage());
            return false;
        }
    }

    /**
     * Solicitud al servidor para subir la imagen seleccionada
     *
     * @param context        El contexto de la aplicación.
     * @param imageUri       La URI de la imagen a subir.
     * @param uploadCallback Callback que recibe la URL de la imagen subida o maneja errores.
     */
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
                    uploadCallback.onUploaded(fotoUrl.getUrl().replace("{ipAndPort}", SOCKET_ADDR));
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

    /**
     * Obtiene la ruta absoluta de un archivo en el sistema a partir de su URI.
     *
     * @param context El contexto de la aplicación.
     * @param uri     La URI de la imagen.
     * @return La ruta real del archivo en el sistema.
     */
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

    /**
     * Carga una imagen en un ImageView desde una URL, con placeholder y manejo de errores usando la
     * libreria de Glide.
     *
     * @param context      El contexto de la aplicación.
     * @param imageView    El ImageView donde se mostrará la imagen.
     * @param url          La URL de la imagen a cargar.
     * @param loadCallback Callback que notifica si la imagen se cargó correctamente.
     */
    public static void setImageViewPhoto(Context context, ImageView imageView, String url, LoadCallback loadCallback) {
        GlideApp.with(context).load(url).placeholder(R.drawable.loading).override(1024, 1024).error(R.drawable.default_user).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                Toast.makeText(context, "La subida de la foto ha fallado", Toast.LENGTH_SHORT).show();
                if (loadCallback != null) loadCallback.onUrlLoaded(false);
                return false;
            }

            @Override
            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                if (loadCallback != null) loadCallback.onUrlLoaded(true);
                return false;
            }
        }).into(imageView);
    }

    /**
     * Callback para manejar la respuesta de la subida de una imagen.
     */
    public interface UploadCallback {
        void onUploaded(String fotoUrl);
    }
    
    /**
     * Callback para manejar el estado de la carga de una imagen en un ImageView.
     */
    public interface LoadCallback {
        void onUrlLoaded(boolean success);
    }
}