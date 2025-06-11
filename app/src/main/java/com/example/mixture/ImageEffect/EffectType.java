package com.example.mixture.ImageEffect;

public enum EffectType {
    NONE("无效果"),
    GRAYSCALE("黑白"),
    SEPIA("怀旧"),
    NEGATIVE("反色"),
    BLUR("模糊"),
    SHARPEN("锐化"),
    EMBOSS("浮雕"),
    VINTAGE("复古"),
    COLD("冷色调"),
    WARM("暖色调"),
    BRIGHTNESS("高亮"),
    CONTRAST("高对比度");

    private final String displayName;

    EffectType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
