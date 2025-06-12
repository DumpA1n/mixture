package com.example.mixture;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mixture.ShaderEffect.ShaderImageProcessor;
import com.example.mixture.ShaderEffect.ShaderImageView;

public class ShaderExampleActivity extends Activity {
    private ShaderImageView shaderImageView;
    private Bitmap originalBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();

        // 加载示例图片
        originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.filter_24px);
        shaderImageView.setImageBitmap(originalBitmap);
    }

    private void setupUI() {
        // 创建布局
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);

        // 创建ShaderImageView
        shaderImageView = new ShaderImageView(this);
        android.widget.LinearLayout.LayoutParams imageParams =
                new android.widget.LinearLayout.LayoutParams(800, 600);
        layout.addView(shaderImageView, imageParams);

        // 创建按钮容器
        android.widget.LinearLayout buttonLayout = new android.widget.LinearLayout(this);
        buttonLayout.setOrientation(android.widget.LinearLayout.HORIZONTAL);

        // 原图按钮
        Button originalBtn = new Button(this);
        originalBtn.setText("原图");
        originalBtn.setOnClickListener(v -> applyOriginalShader());
        buttonLayout.addView(originalBtn);

        // 灰度效果按钮
        Button grayscaleBtn = new Button(this);
        grayscaleBtn.setText("灰度");
        grayscaleBtn.setOnClickListener(v -> applyGrayscaleShader());
        buttonLayout.addView(grayscaleBtn);

        // 模糊效果按钮
        Button blurBtn = new Button(this);
        blurBtn.setText("模糊");
        blurBtn.setOnClickListener(v -> applyBlurShader());
        buttonLayout.addView(blurBtn);

        // 边缘检测按钮
        Button edgeBtn = new Button(this);
        edgeBtn.setText("边缘检测");
        edgeBtn.setOnClickListener(v -> applyEdgeDetectionShader());
        buttonLayout.addView(edgeBtn);

        // 波浪效果按钮
        Button waveBtn = new Button(this);
        waveBtn.setText("波浪动画");
        waveBtn.setOnClickListener(v -> applyWaveShader());
        buttonLayout.addView(waveBtn);

        layout.addView(buttonLayout);
        setContentView(layout);
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