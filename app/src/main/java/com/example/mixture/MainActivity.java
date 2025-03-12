package com.example.mixture;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "DUMPA1N";

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData(); // 获取图片URI
                    if (imageUri != null){
                        Intent intent = new Intent(MainActivity.this, PictureEditActivity.class);
                        intent.putExtra("imageUri", imageUri.toString());
                        startActivity(intent);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        copyAssets(this, "models", getFilesDir().getAbsolutePath() + "/models");

        findViewById(R.id.renderer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RendererActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.pictureEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
            }
        });
    }

    public static void copyAssets(Context context, String assetPath, String targetPath) {
        AssetManager assetManager = context.getAssets();
        try {
            String[] assets = assetManager.list(assetPath);
            if (assets != null && assets.length > 0) {
                File targetDir = new File(targetPath);
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }
                for (String file : assets) {
                    copyAssets(context, assetPath + "/" + file, targetPath + "/" + file);
                }
            } else {
                copyAssetFile(context, assetPath, targetPath);
                Log.i(TAG, "copyAssets: " + assetPath + " to " + targetPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyAssetFile(Context context, String assetPath, String targetPath) {
        AssetManager assetManager = context.getAssets();
        File outFile = new File(targetPath);

        try (InputStream in = assetManager.open(assetPath);
             OutputStream out = new FileOutputStream(outFile)) {

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}