package com.example.mixture.UniversalItems;

public class ItemViewModel {
    public int imageResId;
    public String text;
    public String actionType;

    public ItemViewModel(int imageResId, String text, String actionType) {
        this.imageResId = imageResId;
        this.text = text;
        this.actionType = actionType;
    }
}
