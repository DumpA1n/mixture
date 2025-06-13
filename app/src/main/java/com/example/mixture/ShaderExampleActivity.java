package com.example.mixture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mixture.ShaderEffect.ShaderImageProcessor;
import com.example.mixture.ShaderEffect.ShaderImageView;
import com.example.mixture.UniversalItems.ItemAdapter;
import com.example.mixture.UniversalItems.ItemViewModel;
import com.example.tinyrenderer.NativeLib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShaderExampleActivity extends AppCompatActivity {
    private ShaderImageView shaderImageView;
    private Bitmap originalBitmap;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shader_example);

        shaderImageView = findViewById(R.id.shaderImageView);

        // 加载图片
        loadBitmapFromIntent();

        recyclerView = findViewById(R.id.recyclerView_shader_picker);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<ItemViewModel> itemList = new ArrayList<>();
        itemList.add(new ItemViewModel(R.drawable.check_circle_24px, "原图", "Original"));
        itemList.add(new ItemViewModel(R.drawable.check_circle_24px, "灰度", "Grayscale"));

        ItemAdapter itemAdapter = new ItemAdapter(itemList, item -> {
            switch (item.actionType) {
                case "Original":
                    applyOriginalShader();
                    Toast.makeText(getApplicationContext(), "原图", Toast.LENGTH_LONG).show();
                    break;
                case "Grayscale":
                    applyGrayscaleShader();
                    Toast.makeText(getApplicationContext(), "灰度", Toast.LENGTH_LONG).show();
                    break;
            }
        });
        recyclerView.setAdapter(itemAdapter);
    }

    private void adjustShaderViewRatio(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float ratio = (float) width / height;

        ViewGroup.LayoutParams params = shaderImageView.getLayoutParams();

        int fixedWidth = shaderImageView.getWidth();
        if (fixedWidth == 0) fixedWidth = getResources().getDisplayMetrics().widthPixels;

        params.width = fixedWidth;
        params.height = (int) (fixedWidth / ratio);
        shaderImageView.setLayoutParams(params);
    }

    private void loadBitmapFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String imageUriExtra = intent.getStringExtra("imageUri");
            if (imageUriExtra != null) {
                Uri imageUri = Uri.parse(imageUriExtra);
                try {
                    originalBitmap = getBitmapFromUri(this, imageUri);
                    shaderImageView.setImageBitmap(originalBitmap);
                    adjustShaderViewRatio(originalBitmap);
                } catch (Exception e) {
                    Toast.makeText(this, "加载图片失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
    }

    /**
     * 应用原图着色器
     */
    private void applyOriginalShader() {
        shaderImageView.stopAnimation();
        shaderImageView.setShaders(null, null); // 使用默认着色器
    }

    /**
     * 应用灰度效果着色器
     */
    private void applyGrayscaleShader() {
        shaderImageView.stopAnimation();

        String fragmentShader =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "void main() {\n" +
                        "    vec4 color = texture2D(u_Texture, v_TexCoord);\n" +
                        "    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));\n" +
                        "    gl_FragColor = vec4(gray, gray, gray, color.a);\n" +
                        "}";

        shaderImageView.setShaders(null, fragmentShader);
    }

    /**
     * 应用模糊效果着色器
     */
    private void applyBlurShader() {
        shaderImageView.stopAnimation();

        String fragmentShader =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "uniform vec2 u_Resolution;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    vec2 texelSize = 1.0 / u_Resolution;\n" +
                        "    vec4 color = vec4(0.0);\n" +
                        "    \n" +
                        "    // 5x5 高斯模糊\n" +
                        "    for(int x = -2; x <= 2; x++) {\n" +
                        "        for(int y = -2; y <= 2; y++) {\n" +
                        "            vec2 offset = vec2(float(x), float(y)) * texelSize * 2.0;\n" +
                        "            color += texture2D(u_Texture, v_TexCoord + offset);\n" +
                        "        }\n" +
                        "    }\n" +
                        "    \n" +
                        "    gl_FragColor = color / 25.0;\n" +
                        "}";

        ShaderImageProcessor.ShaderParams params = new ShaderImageProcessor.ShaderParams();
        params.resolutionX = originalBitmap.getWidth();
        params.resolutionY = originalBitmap.getHeight();

        shaderImageView.setShaders(null, fragmentShader);
        shaderImageView.setShaderParams(params);
    }

    /**
     * 应用边缘检测着色器
     */
    private void applyEdgeDetectionShader() {
        shaderImageView.stopAnimation();

        String fragmentShader =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "uniform vec2 u_Resolution;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    vec2 texelSize = 1.0 / u_Resolution;\n" +
                        "    \n" +
                        "    // Sobel边缘检测\n" +
                        "    vec4 tl = texture2D(u_Texture, v_TexCoord + vec2(-texelSize.x, -texelSize.y));\n" +
                        "    vec4 tm = texture2D(u_Texture, v_TexCoord + vec2(0.0, -texelSize.y));\n" +
                        "    vec4 tr = texture2D(u_Texture, v_TexCoord + vec2(texelSize.x, -texelSize.y));\n" +
                        "    vec4 ml = texture2D(u_Texture, v_TexCoord + vec2(-texelSize.x, 0.0));\n" +
                        "    vec4 mr = texture2D(u_Texture, v_TexCoord + vec2(texelSize.x, 0.0));\n" +
                        "    vec4 bl = texture2D(u_Texture, v_TexCoord + vec2(-texelSize.x, texelSize.y));\n" +
                        "    vec4 bm = texture2D(u_Texture, v_TexCoord + vec2(0.0, texelSize.y));\n" +
                        "    vec4 br = texture2D(u_Texture, v_TexCoord + vec2(texelSize.x, texelSize.y));\n" +
                        "    \n" +
                        "    vec4 sobelX = tl + 2.0*ml + bl - tr - 2.0*mr - br;\n" +
                        "    vec4 sobelY = tl + 2.0*tm + tr - bl - 2.0*bm - br;\n" +
                        "    \n" +
                        "    vec4 sobel = sqrt(sobelX * sobelX + sobelY * sobelY);\n" +
                        "    float edge = dot(sobel.rgb, vec3(0.299, 0.587, 0.114));\n" +
                        "    \n" +
                        "    gl_FragColor = vec4(edge, edge, edge, 1.0);\n" +
                        "}";

        ShaderImageProcessor.ShaderParams params = new ShaderImageProcessor.ShaderParams();
        params.resolutionX = originalBitmap.getWidth();
        params.resolutionY = originalBitmap.getHeight();

        shaderImageView.setShaders(null, fragmentShader);
        shaderImageView.setShaderParams(params);
    }

    /**
     * 应用波浪动画着色器
     */
    private void applyWaveShader() {
        String vertexShader =
                "attribute vec4 a_Position;\n" +
                        "attribute vec2 a_TexCoord;\n" +
                        "uniform mat4 u_MVPMatrix;\n" +
                        "uniform float u_Time;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    vec4 pos = a_Position;\n" +
                        "    pos.x += sin(a_Position.y * 10.0 + u_Time * 3.0) * 0.1;\n" +
                        "    gl_Position = u_MVPMatrix * pos;\n" +
                        "    v_TexCoord = a_TexCoord;\n" +
                        "}";

        String fragmentShader =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "uniform float u_Time;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    vec2 coord = v_TexCoord;\n" +
                        "    coord.x += sin(v_TexCoord.y * 20.0 + u_Time * 5.0) * 0.02;\n" +
                        "    coord.y += cos(v_TexCoord.x * 15.0 + u_Time * 4.0) * 0.02;\n" +
                        "    \n" +
                        "    vec4 color = texture2D(u_Texture, coord);\n" +
                        "    \n" +
                        "    // 添加色彩变化\n" +
                        "    color.r += sin(u_Time * 2.0) * 0.1;\n" +
                        "    color.g += cos(u_Time * 1.5) * 0.1;\n" +
                        "    color.b += sin(u_Time * 2.5) * 0.1;\n" +
                        "    \n" +
                        "    gl_FragColor = color;\n" +
                        "}";

        shaderImageView.setShaders(vertexShader, fragmentShader);
        shaderImageView.startAnimation(); // 开始动画
    }

    @Override
    protected void onPause() {
        super.onPause();
        shaderImageView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        shaderImageView.onResume();
    }
}