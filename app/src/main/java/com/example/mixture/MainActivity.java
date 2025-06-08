package com.example.mixture;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "DUMPA1N";

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

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.nav_profile) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MineFragment())
                        .commit();
                return true;
            }
            return false;
        });

        copyAssets(this, "models", getFilesDir().getAbsolutePath() + "/models");
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