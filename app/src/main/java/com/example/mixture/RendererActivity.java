package com.example.mixture;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tinyrenderer.NativeLib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Objects;

import javax.security.auth.login.LoginException;

public class RendererActivity extends AppCompatActivity {
    private static String TAG = "DUMPA1N";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_renderer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        copyAssets(this, "models", getFilesDir().getAbsolutePath() + "/models");

        SurfaceView surfaceView = findViewById(R.id.surfaceView);

        NativeLib n = new NativeLib();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                if (n.isRendering() == 1) {
                    n.stopRender(surfaceView.getHolder().getSurface());
                    Toast.makeText(getApplicationContext(), "停止渲染", Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.render_africa_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                n.stopRender(surfaceView.getHolder().getSurface());
                if (n.isRendering() == 0) {
                    n.startRender(surfaceView.getHolder().getSurface(), "africa_head");
                    Toast.makeText(getApplicationContext(), "开始渲染 [非洲头]", Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.render_spot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                n.stopRender(surfaceView.getHolder().getSurface());
                if (n.isRendering() == 0) {
                    n.startRender(surfaceView.getHolder().getSurface(), "spot");
                    Toast.makeText(getApplicationContext(), "开始渲染 [牛牛]", Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.render_diablo3_pose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                n.stopRender(surfaceView.getHolder().getSurface());
                if (n.isRendering() == 0) {
                    n.startRender(surfaceView.getHolder().getSurface(), "diablo3_pose");
                    Toast.makeText(getApplicationContext(), "开始渲染 [暗黑破坏神]", Toast.LENGTH_LONG).show();
                }
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