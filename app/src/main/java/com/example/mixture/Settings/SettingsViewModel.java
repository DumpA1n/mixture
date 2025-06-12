package com.example.mixture.Settings;

public class SettingsViewModel {
    public int iconResId;
    public String title;
    public boolean hasSwitch;
    public boolean switchState;
    private String description;

    public SettingsViewModel(int iconResId, String title, boolean hasSwitch, boolean switchState, String description) {
        this.iconResId = iconResId;
        this.title = title;
        this.hasSwitch = hasSwitch;
        this.switchState = switchState;
        this.description = description;
    }

    public SettingsViewModel(int iconResId, String title, boolean hasSwitch, boolean switchValue) {
        this(iconResId, title, hasSwitch, switchValue, null);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
