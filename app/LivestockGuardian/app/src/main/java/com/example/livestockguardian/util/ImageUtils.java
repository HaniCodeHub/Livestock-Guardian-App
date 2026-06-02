package com.example.livestockguardian.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ImageUtils {

    private ImageUtils() {
    }

    public static byte[] bitmapToJpegBytes(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    public static String bitmapToDataUrl(Bitmap bitmap, int quality) {
        byte[] bytes = bitmapToJpegBytes(bitmap, quality);
        return "data:image/jpeg;base64," + android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP);
    }

    public static Bitmap loadBitmap(Context context, Uri uri, int maxSize) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new IOException("Unable to open image");
        }
        Bitmap decoded = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        if (decoded == null) {
            throw new IOException("Unable to decode image");
        }
        return scaleDown(decoded, maxSize);
    }

    public static Bitmap scaleDown(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width <= maxSize && height <= maxSize) {
            return bitmap;
        }
        float scale = Math.min((float) maxSize / width, (float) maxSize / height);
        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}
