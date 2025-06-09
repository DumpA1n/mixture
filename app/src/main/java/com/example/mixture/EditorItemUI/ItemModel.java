package com.example.mixture.EditorItemUI;

public class ItemModel {
    public int imageResId;
    public String text;
    public String actionType; // 新增：标识操作类型，如 "rotate", "scale_up", "crop"

    public ItemModel(int imageResId, String text, String actionType) {
        this.imageResId = imageResId;
        this.text = text;
        this.actionType = actionType;
    }
}
