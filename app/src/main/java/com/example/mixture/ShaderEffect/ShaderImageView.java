package com.example.mixture.ShaderEffect;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 支持自定义Shader的ImageView
 * 基于OpenGL ES渲染
 */
public class ShaderImageView extends GLSurfaceView {
    private ShaderRenderer renderer;
    private Bitmap bitmap;

    public ShaderImageView(Context context) {
        super(context);
        init();
    }

    public ShaderImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        renderer = new ShaderRenderer();
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    /**
     * 设置要显示的图像
     */
    public void setImageBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        if (renderer != null) {
            renderer.setBitmap(bitmap);
            requestRender();
        }
    }

    /**
     * 设置自定义着色器
     */
    public void setShaders(String vertexShader, String fragmentShader) {
        if (renderer != null) {
            renderer.setShaders(vertexShader, fragmentShader);
            requestRender();
        }
    }

    /**
     * 设置着色器参数
     */
    public void setShaderParams(ShaderImageProcessor.ShaderParams params) {
        if (renderer != null) {
            renderer.setShaderParams(params);
            requestRender();
        }
    }

    /**
     * 开始动画（定时更新渲染）
     */
    public void startAnimation() {
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    /**
     * 停止动画
     */
    public void stopAnimation() {
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    /**
     * GL渲染器
     */
    private class ShaderRenderer implements GLSurfaceView.Renderer {
        private ShaderImageProcessor processor;
        private Bitmap bitmap;
        private int textureId = -1;
        private boolean needsUpdate = false;
        private long startTime;

        public ShaderRenderer() {
            processor = new ShaderImageProcessor();
            startTime = System.currentTimeMillis();
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            this.needsUpdate = true;
        }

        public void setShaders(String vertexShader, String fragmentShader) {
            processor.setShaders(vertexShader, fragmentShader);
            this.needsUpdate = true;
        }

        public void setShaderParams(ShaderImageProcessor.ShaderParams params) {
            processor.setShaderParams(params);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            processor.initGL();
            needsUpdate = true;
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            // 视口已在processor.render中设置
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            if (bitmap != null) {
                // 如果需要更新纹理
                if (needsUpdate) {
                    if (textureId != -1) {
                        // 删除旧纹理
                        int[] textures = {textureId};
                        gl.glDeleteTextures(1, textures, 0);
                    }
                    textureId = processor.loadTexture(bitmap);
                    needsUpdate = false;
                }

                if (textureId != -1) {
                    // 更新时间参数（用于动画）
                    ShaderImageProcessor.ShaderParams params = processor.shaderParams;
                    if (params != null) {
                        params.time = (System.currentTimeMillis() - startTime) / 1000.0f;
                        params.resolutionX = getWidth();
                        params.resolutionY = getHeight();
                    }

                    processor.render(textureId, getWidth(), getHeight());
                }
            }
        }
    }
}
