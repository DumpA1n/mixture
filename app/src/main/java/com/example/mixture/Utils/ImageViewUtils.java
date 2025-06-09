package com.example.mixture.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageViewUtils {
    public static Uri getImageViewUri(Context context, ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable == null) return null;

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        File file = new File(context.getCacheDir(), "temp_image.jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return Uri.fromFile(file); // 不用 FileProvider
    }
}
