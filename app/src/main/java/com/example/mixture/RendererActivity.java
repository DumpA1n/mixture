package com.example.mixture;

import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tinyrenderer.NativeLib;

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

        Spinner spinner_AAMode = findViewById(R.id.spinner_AAMode);
        String[] options_AAMode = {"默认", "4xMSAA", "4xSSAA", "FXAA", "TAA"};
        ArrayAdapter<String> adapter_AAMode = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options_AAMode);
        adapter_AAMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_AAMode.setAdapter(adapter_AAMode);
        spinner_AAMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) { n.setAAMode(false, false, false, false); }
                if (pos == 1) { n.setAAMode(true, false, false, false); }
                if (pos == 2) { n.setAAMode(false, true, false, false); }
                if (pos == 3) { n.setAAMode(false, false, true, false); }
                if (pos == 4) { n.setAAMode(false, false, false, true); }
                Toast.makeText(getApplicationContext(), "选择了: " + options_AAMode[pos], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Spinner spinner_RenderMode = findViewById(R.id.spinner_RenderMode);
        String[] options_RenderMode = {"纹理模式", "线框模式", "纯色模式"};
        ArrayAdapter<String> adapter_RenderMode = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options_RenderMode);
        adapter_RenderMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_RenderMode.setAdapter(adapter_RenderMode);
        spinner_RenderMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                n.setRenderMode(pos + 1);
                Toast.makeText(getApplicationContext(), "选择了: " + options_RenderMode[pos], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}