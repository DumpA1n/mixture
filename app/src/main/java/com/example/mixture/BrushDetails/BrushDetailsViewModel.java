package com.example.mixture.BrushDetails;

import android.widget.SeekBar;
import android.widget.TextView;

public class BrushDetailsViewModel {
    public String label;
    public int value;

    public BrushDetailsViewModel(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
