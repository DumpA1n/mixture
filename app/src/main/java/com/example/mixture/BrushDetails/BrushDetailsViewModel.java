package com.example.mixture.BrushDetails;

import android.widget.SeekBar;
import android.widget.TextView;

public class BrushDetailsViewModel {
    TextView textView;
    SeekBar seekBar;
    public BrushDetailsViewModel(TextView textView, SeekBar seekBar) {
        this.textView = textView;
        this.seekBar = seekBar;
    }
}
