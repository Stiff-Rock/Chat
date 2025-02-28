package com.stiffrock.chat.utils;

import static com.stiffrock.chat.utils.LogTag.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageManager {
    public static final int MAX_IMAGE_SIZE_MB = 10;
    private static final int MAX_DIMENSION = 1024;

    public static void openGallery(ActivityResultLauncher<Intent> launcher) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    @Nullable
    public static Bitmap uriToBitmap(Context context, Uri uri) {
        try {
            if (!isValidImage(context, uri)) return null;

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            options.inSampleSize = calculateInSampleSize(options);
            options.inJustDecodeBounds = false;

            inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            return bitmap;
        } catch (IOException e) {
            Log.e(TAG, "Error loading image: " + e.getMessage());
            Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static boolean isValidImage(Context context, Uri uri) {
        try {
            String mimeType = context.getContentResolver().getType(uri);
            if (!"image/jpeg".equals(mimeType) && !"image/png".equals(mimeType)) {
                Toast.makeText(context, "Invalid format. Use JPG/PNG", Toast.LENGTH_SHORT).show();
                return false;
            }

            InputStream inputStream = context.getContentResolver().openInputStream(uri);

            if (inputStream == null) return false;

            int fileSize = inputStream.available();
            inputStream.close();


            if (fileSize > MAX_IMAGE_SIZE_MB * 1024 * 1024) {
                Toast.makeText(context, "Image too large (Max 10MB)", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Validation error: " + e.getMessage());
            return false;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > MAX_DIMENSION || width > MAX_DIMENSION) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= MAX_DIMENSION && (halfWidth / inSampleSize) >= MAX_DIMENSION) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap == null) return new byte[0];

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }

    public static Bitmap getBitmapFromBytes(byte[] photoBytes) {
        if (photoBytes == null || photoBytes.length == 0) return null;
        return BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
    }
}