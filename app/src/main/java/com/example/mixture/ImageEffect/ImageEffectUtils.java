package com.example.mixture.ImageEffect;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.widget.ImageView;

public class ImageEffectUtils {

    public static void applyEffect(ImageView imageView, EffectType effectType) {
        switch (effectType) {
            case NONE:
                clearEffects(imageView);
                break;
            case GRAYSCALE:
                applyGrayscale(imageView);
                break;
            case SEPIA:
                applySepia(imageView);
                break;
            case NEGATIVE:
                applyNegative(imageView);
                break;
            case BLUR:
                applyBlur(imageView);
                break;
            case VINTAGE:
                applyVintage(imageView);
                break;
            case COLD:
                applyColdTone(imageView);
                break;
            case WARM:
                applyWarmTone(imageView);
                break;
            case BRIGHTNESS:
                applyBrightness(imageView, 50f);
                break;
            case CONTRAST:
                applyContrast(imageView, 1.5f);
                break;
        }
    }

    private static void clearEffects(ImageView imageView) {
        imageView.setColorFilter(null);
    }

    private static void applyGrayscale(ImageView imageView) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        imageView.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private static void applySepia(ImageView imageView) {
        ColorMatrix matrix = new ColorMatrix(new float[]{
                0.393f, 0.769f, 0.189f, 0, 0,
                0.349f, 0.686f, 0.168f, 0, 0,
                0.272f, 0.534f, 0.131f, 0, 0,
                0, 0, 0, 1, 0
        });
        imageView.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private static void applyNegative(ImageView imageView) {
        ColorMatrix matrix = new ColorMatrix(new float[]{
                -1, 0, 0, 0, 255,
                0, -1, 0, 0, 255,
                0, 0, -1, 0, 255,
                0, 0, 0, 1, 0
        });
        imageView.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private static void applyBlur(ImageView imageView) {
        // 注意：这里需要使用 RenderScript 或其他库来实现真正的模糊效果
        // 这里只是一个简化的实现
        ColorMatrix matrix = new ColorMatrix(new float[]{
                0.1f, 0.1f, 0.1f, 0, 0,
                0.1f, 0.1f, 0.1f, 0, 0,
                0.1f, 0.1f, 0.1f, 0, 0,
                0, 0, 0, 1, 0
        });
        imageView.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private static void applyVintage(ImageView imageView) {
        ColorMatrix matrix = new ColorMatrix(new float[]{
                1.2f, 0, 0, 0, -20,
                0, 1.1f, 0, 0, -10,
                0, 0, 0.8f, 0, 20,
                0, 0, 0, 1, 0
        });
        imageView.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private static void applyColdTone(ImageView imageView) {
        ColorMatrix matrix = new ColorMatrix(new float[]{
                0.8f, 0, 0.2f, 0, 0,
                0, 0.9f, 0.1f, 0, 0,
                0.2f, 0.1f, 1.2f, 0, 0,
                0, 0, 0, 1, 0
        });
        imageView.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private static void applyWarmTone(ImageView imageView) {
        ColorMatrix matrix = new ColorMatrix(new float[]{
                1.2f, 0.1f, 0, 0, 0,
                0.1f, 1.1f, 0, 0, 0,
                0, 0, 0.8f, 0, 0,
                0, 0, 0, 1, 0
        });
        imageView.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private static void applyBrightness(ImageView imageView, float brightness) {
        ColorMatrix matrix = new ColorMatrix(new float[]{
                1, 0, 0, 0, brightness,
                0, 1, 0, 0, brightness,
                0, 0, 1, 0, brightness,
                0, 0, 0, 1, 0
        });
        imageView.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private static void applyContrast(ImageView imageView, float contrast) {
        float scale = contrast;
        float translate = (-.5f * scale + .5f) * 255f;

        ColorMatrix matrix = new ColorMatrix(new float[]{
                scale, 0, 0, 0, translate,
                0, scale, 0, 0, translate,
                0, 0, scale, 0, translate,
                0, 0, 0, 1, 0
        });
        imageView.setColorFilter(new ColorMatrixColorFilter(matrix));
    }
}