package com.example.mixture.ShaderEffect;

/**
 * 预定义的Shader效果管理器
 * 提供常用的图像处理效果
 */
public class ShaderEffectsManager {

    // 效果类型枚举
    public enum EffectType {
        ORIGINAL,           // 原图
        GRAYSCALE,         // 灰度
        SEPIA,             // 复古
        BLUR,              // 模糊
        EDGE_DETECTION,    // 边缘检测
        EMBOSS,            // 浮雕
        SHARPEN,           // 锐化
        INVERT,            // 反色
        BRIGHTNESS,        // 亮度调节
        CONTRAST,          // 对比度调节
        SATURATION,        // 饱和度调节
        VIGNETTE,          // 暗角效果
        FISHEYE,           // 鱼眼效果
        WAVE_DISTORTION,   // 波浪扭曲
        PIXELATE,          // 像素化
        OIL_PAINTING,      // 油画效果
        WATER_RIPPLE,      // 水波纹动画
        GLITCH             // 故障艺术效果
    }

    /**
     * 获取指定效果的着色器代码
     */
    public static ShaderConfig getShaderConfig(EffectType effectType) {
        switch (effectType) {
            case ORIGINAL:
                return getOriginalShader();
            case GRAYSCALE:
                return getGrayscaleShader();
            case SEPIA:
                return getSepiaShader();
            case BLUR:
                return getBlurShader();
            case EDGE_DETECTION:
                return getEdgeDetectionShader();
            case EMBOSS:
                return getEmbossShader();
            case SHARPEN:
                return getSharpenShader();
            case INVERT:
                return getInvertShader();
            case BRIGHTNESS:
                return getBrightnessShader();
            case CONTRAST:
                return getContrastShader();
            case SATURATION:
                return getSaturationShader();
            case VIGNETTE:
                return getVignetteShader();
            case FISHEYE:
                return getFisheyeShader();
            case WAVE_DISTORTION:
                return getWaveDistortionShader();
            case PIXELATE:
                return getPixelateShader();
            case OIL_PAINTING:
                return getOilPaintingShader();
            case WATER_RIPPLE:
                return getWaterRippleShader();
            case GLITCH:
                return getGlitchShader();
            default:
                return getOriginalShader();
        }
    }

    private static ShaderConfig getOriginalShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "void main() {\n" +
                        "    gl_FragColor = texture2D(u_Texture, v_TexCoord);\n" +
                        "}";
        return new ShaderConfig(null, fragment, false);
    }

    private static ShaderConfig getGrayscaleShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "void main() {\n" +
                        "    vec4 color = texture2D(u_Texture, v_TexCoord);\n" +
                        "    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));\n" +
                        "    gl_FragColor = vec4(gray, gray, gray, color.a);\n" +
                        "}";
        return new ShaderConfig(null, fragment, false);
    }

    private static ShaderConfig getSepiaShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "void main() {\n" +
                        "    vec4 color = texture2D(u_Texture, v_TexCoord);\n" +
                        "    float r = color.r * 0.393 + color.g * 0.769 + color.b * 0.189;\n" +
                        "    float g = color.r * 0.349 + color.g * 0.686 + color.b * 0.168;\n" +
                        "    float b = color.r * 0.272 + color.g * 0.534 + color.b * 0.131;\n" +
                        "    gl_FragColor = vec4(r, g, b, color.a);\n" +
                        "}";
        return new ShaderConfig(null, fragment, false);
    }

    private static ShaderConfig getBlurShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "uniform vec2 u_Resolution;\n" +
                        "uniform float u_BlurRadius;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    vec2 texelSize = 1.0 / u_Resolution;\n" +
                        "    vec4 color = vec4(0.0);\n" +
                        "    float radius = u_BlurRadius;\n" +
                        "    \n" +
                        "    for(float x = -radius; x <= radius; x++) {\n" +
                        "        for(float y = -radius; y <= radius; y++) {\n" +
                        "            vec2 offset = vec2(x, y) * texelSize;\n" +
                        "            color += texture2D(u_Texture, v_TexCoord + offset);\n" +
                        "        }\n" +
                        "    }\n" +
                        "    \n" +
                        "    float samples = (radius * 2.0 + 1.0) * (radius * 2.0 + 1.0);\n" +
                        "    gl_FragColor = color / samples;\n" +
                        "}";
        return new ShaderConfig(null, fragment, false);
    }

    private static ShaderConfig getEdgeDetectionShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "uniform vec2 u_Resolution;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    vec2 texelSize = 1.0 / u_Resolution;\n" +
                        "    \n" +
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
        return new ShaderConfig(null, fragment, false);
    }

    private static ShaderConfig getVignetteShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "uniform float u_VignetteStrength;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    vec4 color = texture2D(u_Texture, v_TexCoord);\n" +
                        "    vec2 center = vec2(0.5, 0.5);\n" +
                        "    float dist = distance(v_TexCoord, center);\n" +
                        "    float vignette = 1.0 - smoothstep(0.3, 0.8, dist * u_VignetteStrength);\n" +
                        "    gl_FragColor = vec4(color.rgb * vignette, color.a);\n" +
                        "}";
        return new ShaderConfig(null, fragment, false);
    }

    private static ShaderConfig getFisheyeShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "uniform float u_FisheyeStrength;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    vec2 center = vec2(0.5, 0.5);\n" +
                        "    vec2 coord = v_TexCoord - center;\n" +
                        "    float dist = length(coord);\n" +
                        "    \n" +
                        "    if (dist < 0.5) {\n" +
                        "        float factor = 1.0 + u_FisheyeStrength * dist * dist;\n" +
                        "        coord = coord * factor + center;\n" +
                        "        gl_FragColor = texture2D(u_Texture, coord);\n" +
                        "    } else {\n" +
                        "        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);\n" +
                        "    }\n" +
                        "}";
        return new ShaderConfig(null, fragment, false);
    }

    private static ShaderConfig getPixelateShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "uniform vec2 u_Resolution;\n" +
                        "uniform float u_PixelSize;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    vec2 pixelSize = vec2(u_PixelSize) / u_Resolution;\n" +
                        "    vec2 coord = floor(v_TexCoord / pixelSize) * pixelSize;\n" +
                        "    gl_FragColor = texture2D(u_Texture, coord);\n" +
                        "}";
        return new ShaderConfig(null, fragment, false);
    }

    private static ShaderConfig getWaterRippleShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "uniform float u_Time;\n" +
                        "uniform vec2 u_RippleCenter;\n" +
                        "uniform float u_RippleStrength;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    vec2 center = u_RippleCenter;\n" +
                        "    float dist = distance(v_TexCoord, center);\n" +
                        "    float ripple = sin(dist * 30.0 - u_Time * 5.0) * u_RippleStrength;\n" +
                        "    ripple *= exp(-dist * 3.0); // 衰减\n" +
                        "    \n" +
                        "    vec2 direction = normalize(v_TexCoord - center);\n" +
                        "    vec2 coord = v_TexCoord + direction * ripple * 0.01;\n" +
                        "    \n" +
                        "    gl_FragColor = texture2D(u_Texture, coord);\n" +
                        "}";
        return new ShaderConfig(null, fragment, true); // 需要动画
    }

    private static ShaderConfig getGlitchShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "uniform float u_Time;\n" +
                        "uniform float u_GlitchStrength;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "\n" +
                        "float random(vec2 st) {\n" +
                        "    return fract(sin(dot(st.xy, vec2(12.9898,78.233))) * 43758.5453123);\n" +
                        "}\n" +
                        "\n" +
                        "void main() {\n" +
                        "    vec2 coord = v_TexCoord;\n" +
                        "    \n" +
                        "    // 随机水平偏移\n" +
                        "    float noise = random(vec2(floor(coord.y * 100.0), u_Time));\n" +
                        "    if (noise > 0.95) {\n" +
                        "        coord.x += (random(vec2(u_Time, coord.y)) - 0.5) * u_GlitchStrength;\n" +
                        "    }\n" +
                        "    \n" +
                        "    vec4 color = texture2D(u_Texture, coord);\n" +
                        "    \n" +
                        "    // 颜色通道分离\n" +
                        "    if (random(vec2(u_Time, 0.0)) > 0.8) {\n" +
                        "        color.r = texture2D(u_Texture, coord + vec2(0.01 * u_GlitchStrength, 0.0)).r;\n" +
                        "        color.b = texture2D(u_Texture, coord - vec2(0.01 * u_GlitchStrength, 0.0)).b;\n" +
                        "    }\n" +
                        "    \n" +
                        "    gl_FragColor = color;\n" +
                        "}";
        return new ShaderConfig(null, fragment, true); // 需要动画
    }

    // 其他效果的实现...
    private static ShaderConfig getEmbossShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "uniform vec2 u_Resolution;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    vec2 texelSize = 1.0 / u_Resolution;\n" +
                        "    vec4 tl = texture2D(u_Texture, v_TexCoord + vec2(-texelSize.x, -texelSize.y));\n" +
                        "    vec4 br = texture2D(u_Texture, v_TexCoord + vec2(texelSize.x, texelSize.y));\n" +
                        "    vec4 emboss = (tl - br) + 0.5;\n" +
                        "    gl_FragColor = vec4(emboss.rgb, 1.0);\n" +
                        "}";
        return new ShaderConfig(null, fragment, false);
    }

    private static ShaderConfig getSharpenShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "uniform vec2 u_Resolution;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    vec2 texelSize = 1.0 / u_Resolution;\n" +
                        "    vec4 center = texture2D(u_Texture, v_TexCoord);\n" +
                        "    vec4 up = texture2D(u_Texture, v_TexCoord + vec2(0.0, -texelSize.y));\n" +
                        "    vec4 down = texture2D(u_Texture, v_TexCoord + vec2(0.0, texelSize.y));\n" +
                        "    vec4 left = texture2D(u_Texture, v_TexCoord + vec2(-texelSize.x, 0.0));\n" +
                        "    vec4 right = texture2D(u_Texture, v_TexCoord + vec2(texelSize.x, 0.0));\n" +
                        "    \n" +
                        "    vec4 sharpen = center * 5.0 - up - down - left - right;\n" +
                        "    gl_FragColor = sharpen;\n" +
                        "}";
        return new ShaderConfig(null, fragment, false);
    }

    private static ShaderConfig getInvertShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "void main() {\n" +
                        "    vec4 color = texture2D(u_Texture, v_TexCoord);\n" +
                        "    gl_FragColor = vec4(1.0 - color.rgb, color.a);\n" +
                        "}";
        return new ShaderConfig(null, fragment, false);
    }

    private static ShaderConfig getBrightnessShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "uniform float u_Brightness;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "void main() {\n" +
                        "    vec4 color = texture2D(u_Texture, v_TexCoord);\n" +
                        "    gl_FragColor = vec4(color.rgb + u_Brightness, color.a);\n" +
                        "}";
        return new ShaderConfig(null, fragment, false);
    }

    private static ShaderConfig getContrastShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "uniform float u_Contrast;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "void main() {\n" +
                        "    vec4 color = texture2D(u_Texture, v_TexCoord);\n" +
                        "    gl_FragColor = vec4(((color.rgb - 0.5) * u_Contrast) + 0.5, color.a);\n" +
                        "}";
        return new ShaderConfig(null, fragment, false);
    }

    private static ShaderConfig getSaturationShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "uniform float u_Saturation;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "void main() {\n" +
                        "    vec4 color = texture2D(u_Texture, v_TexCoord);\n" +
                        "    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));\n" +
                        "    gl_FragColor = vec4(mix(vec3(gray), color.rgb, u_Saturation), color.a);\n" +
                        "}";
        return new ShaderConfig(null, fragment, false);
    }

    private static ShaderConfig getWaveDistortionShader() {
        String vertex =
                "attribute vec4 a_Position;\n" +
                        "attribute vec2 a_TexCoord;\n" +
                        "uniform mat4 u_MVPMatrix;\n" +
                        "uniform float u_Time;\n" +
                        "uniform float u_WaveStrength;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    vec4 pos = a_Position;\n" +
                        "    pos.x += sin(a_Position.y * 10.0 + u_Time * 3.0) * u_WaveStrength;\n" +
                        "    gl_Position = u_MVPMatrix * pos;\n" +
                        "    v_TexCoord = a_TexCoord;\n" +
                        "}";

        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "void main() {\n" +
                        "    gl_FragColor = texture2D(u_Texture, v_TexCoord);\n" +
                        "}";
        return new ShaderConfig(vertex, fragment, true);
    }

    private static ShaderConfig getOilPaintingShader() {
        String fragment =
                "precision mediump float;\n" +
                        "uniform sampler2D u_Texture;\n" +
                        "uniform vec2 u_Resolution;\n" +
                        "uniform float u_Radius;\n" +
                        "varying vec2 v_TexCoord;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    vec2 texelSize = 1.0 / u_Resolution;\n" +
                        "    vec4 meanColor = vec4(0.0);\n" +
                        "    int samples = 0;\n" +
                        "    \n" +
                        "    for(float x = -u_Radius; x <= u_Radius; x += 1.0) {\n" +
                        "        for(float y = -u_Radius; y <= u_Radius; y += 1.0) {\n" +
                        "            vec2 offset = vec2(x, y) * texelSize;\n" +
                        "            vec4 color = texture2D(u_Texture, v_TexCoord + offset);\n" +
                        "            meanColor += color;\n" +
                        "            samples++;\n" +
                        "        }\n" +
                        "    }\n" +
                        "    \n" +
                        "    meanColor /= float(samples);\n" +
                        "    gl_FragColor = meanColor;\n" +
                        "}";
        return new ShaderConfig(null, fragment, false);
    }

    /**
     * Shader配置类
     */
    public static class ShaderConfig {
        public final String vertexShader;
        public final String fragmentShader;
        public final boolean needsAnimation;

        public ShaderConfig(String vertexShader, String fragmentShader, boolean needsAnimation) {
            this.vertexShader = vertexShader;
            this.fragmentShader = fragmentShader;
            this.needsAnimation = needsAnimation;
        }
    }
}
