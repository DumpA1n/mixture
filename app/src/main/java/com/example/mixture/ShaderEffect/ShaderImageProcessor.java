package com.example.mixture.ShaderEffect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Shader图像处理器
 * 可以传入自定义的顶点着色器和片段着色器来处理图像
 */
public class ShaderImageProcessor {
    private static final String TAG = "ShaderImageProcessor";

    // 默认顶点着色器
    private static final String DEFAULT_VERTEX_SHADER =
            "attribute vec4 a_Position;\n" +
                    "attribute vec2 a_TexCoord;\n" +
                    "uniform mat4 u_MVPMatrix;\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "void main() {\n" +
                    "    gl_Position = u_MVPMatrix * a_Position;\n" +
                    "    v_TexCoord = a_TexCoord;\n" +
                    "}";

    // 默认片段着色器（原图显示）
    private static final String DEFAULT_FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "uniform sampler2D u_Texture;\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D(u_Texture, v_TexCoord);\n" +
                    "}";

    // 顶点数据
    private static final float[] VERTICES = {
            // 位置坐标        纹理坐标
            -1.0f, -1.0f, 0.0f,  0.0f, 1.0f,
            1.0f, -1.0f, 0.0f,  1.0f, 1.0f,
            -1.0f,  1.0f, 0.0f,  0.0f, 0.0f,
            1.0f,  1.0f, 0.0f,  1.0f, 0.0f
    };

    private FloatBuffer vertexBuffer;
    private int program;
    private int[] textureIds;
    private float[] mvpMatrix = new float[16];
    private String vertexShader;
    private String fragmentShader;

    // Uniform和Attribute位置
    private int positionHandle;
    private int texCoordHandle;
    private int mvpMatrixHandle;
    private int textureHandle;

    // 自定义uniform参数
    ShaderParams shaderParams;

    public ShaderImageProcessor() {
        this.vertexShader = DEFAULT_VERTEX_SHADER;
        this.fragmentShader = DEFAULT_FRAGMENT_SHADER;
        this.shaderParams = new ShaderParams();
        initVertexBuffer();
    }

    /**
     * 设置自定义着色器
     */
    public void setShaders(String vertexShader, String fragmentShader) {
        this.vertexShader = vertexShader != null ? vertexShader : DEFAULT_VERTEX_SHADER;
        this.fragmentShader = fragmentShader != null ? fragmentShader : DEFAULT_FRAGMENT_SHADER;
    }

    /**
     * 设置着色器参数
     */
    public void setShaderParams(ShaderParams params) {
        this.shaderParams = params != null ? params : new ShaderParams();
    }

    private void initVertexBuffer() {
        ByteBuffer bb = ByteBuffer.allocateDirect(VERTICES.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(VERTICES);
        vertexBuffer.position(0);
    }

    /**
     * 初始化OpenGL程序
     */
    public boolean initGL() {
        program = createProgram(vertexShader, fragmentShader);
        if (program == 0) {
            Log.e(TAG, "Failed to create program");
            return false;
        }

        // 获取着色器中的变量位置
        positionHandle = GLES20.glGetAttribLocation(program, "a_Position");
        texCoordHandle = GLES20.glGetAttribLocation(program, "a_TexCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "u_MVPMatrix");
        textureHandle = GLES20.glGetUniformLocation(program, "u_Texture");

        // 初始化变换矩阵为单位矩阵
        Matrix.setIdentityM(mvpMatrix, 0);

        return true;
    }

    /**
     * 加载纹理
     */
    public int loadTexture(Bitmap bitmap) {
        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);

        if (textureIds[0] != 0) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);

            // 设置纹理参数
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            // 加载位图到纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }

        if (textureIds[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureIds[0];
    }

    /**
     * 渲染图像
     */
    public void render(int textureId, int viewportWidth, int viewportHeight) {
        GLES20.glViewport(0, 0, viewportWidth, viewportHeight);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // 使用着色器程序
        GLES20.glUseProgram(program);

        // 设置顶点属性
        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 5 * 4, vertexBuffer);

        vertexBuffer.position(3);
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 5 * 4, vertexBuffer);

        // 设置变换矩阵
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        // 绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(textureHandle, 0);

        // 设置自定义参数
        setCustomUniforms();

        // 绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        // 清理
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    /**
     * 设置自定义uniform参数
     */
    private void setCustomUniforms() {
        if (shaderParams != null) {
            // 设置时间参数（用于动画效果）
            int timeHandle = GLES20.glGetUniformLocation(program, "u_Time");
            if (timeHandle != -1) {
                GLES20.glUniform1f(timeHandle, shaderParams.time);
            }

            // 设置分辨率参数
            int resolutionHandle = GLES20.glGetUniformLocation(program, "u_Resolution");
            if (resolutionHandle != -1) {
                GLES20.glUniform2f(resolutionHandle, shaderParams.resolutionX, shaderParams.resolutionY);
            }

            // 设置自定义浮点参数
            for (String key : shaderParams.floatParams.keySet()) {
                int handle = GLES20.glGetUniformLocation(program, key);
                if (handle != -1) {
                    GLES20.glUniform1f(handle, shaderParams.floatParams.get(key));
                }
            }

            // 设置自定义向量参数
            for (String key : shaderParams.vec2Params.keySet()) {
                int handle = GLES20.glGetUniformLocation(program, key);
                if (handle != -1) {
                    float[] vec = shaderParams.vec2Params.get(key);
                    GLES20.glUniform2f(handle, vec[0], vec[1]);
                }
            }

            // 设置自定义颜色参数
            for (String key : shaderParams.colorParams.keySet()) {
                int handle = GLES20.glGetUniformLocation(program, key);
                if (handle != -1) {
                    float[] color = shaderParams.colorParams.get(key);
                    GLES20.glUniform4f(handle, color[0], color[1], color[2], color[3]);
                }
            }
        }
    }

    /**
     * 创建着色器程序
     */
    private int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragmentShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);
            GLES20.glLinkProgram(program);

            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);

            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: " + GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }

        return program;
    }

    /**
     * 加载着色器
     */
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        if (shader != 0) {
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);

            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);

            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + type + ": " + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }

        return shader;
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        if (program != 0) {
            GLES20.glDeleteProgram(program);
            program = 0;
        }

        if (textureIds != null) {
            GLES20.glDeleteTextures(textureIds.length, textureIds, 0);
            textureIds = null;
        }
    }

    /**
     * 着色器参数类
     */
    public static class ShaderParams {
        public float time = 0.0f;
        public float resolutionX = 1.0f;
        public float resolutionY = 1.0f;

        // 自定义参数映射
        public java.util.Map<String, Float> floatParams = new java.util.HashMap<>();
        public java.util.Map<String, float[]> vec2Params = new java.util.HashMap<>();
        public java.util.Map<String, float[]> colorParams = new java.util.HashMap<>();

        public void setFloat(String name, float value) {
            floatParams.put(name, value);
        }

        public void setVec2(String name, float x, float y) {
            vec2Params.put(name, new float[]{x, y});
        }

        public void setColor(String name, float r, float g, float b, float a) {
            colorParams.put(name, new float[]{r, g, b, a});
        }
    }
}