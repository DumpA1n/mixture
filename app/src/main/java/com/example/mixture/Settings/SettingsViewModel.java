package com.example.mixture.Settings;

public class SettingsViewModel {
    public int iconResId;
    public String title;
    public boolean hasSwitch;
    public boolean switchState;

    public SettingsViewModel(int iconResId, String title, boolean hasSwitch, boolean switchState) {
        this.iconResId = iconResId;
        this.title = title;
        this.hasSwitch = hasSwitch;
        this.switchState = switchState;
    }
}
