package com.example.mixture.Utils;

import java.util.ArrayList;
import java.util.List;

public class ThemeUtils {
    private static List<ThemeChangeCallback> sCallbacks = new ArrayList<ThemeChangeCallback>();
    private static int sCurrentThemeId = 0 ;
    public static int DAY_THEME_ID = 0 ;
    public static int NIGHT_THEME_ID = 1 ;
    private ThemeUtils() {}
    public static void addCallback(ThemeChangeCallback callback) {
        if (callback != null) {
            sCallbacks.add(callback);
        }
    }
    public static void removeCallback(ThemeChangeCallback callback) {
        if (callback != null) {
            sCallbacks.remove(callback);
        }
    }
    public static synchronized void changeTheme() {
        sCurrentThemeId = sCurrentThemeId == DAY_THEME_ID ? NIGHT_THEME_ID : DAY_THEME_ID;
        for (ThemeChangeCallback callback : sCallbacks)
            if (callback != null)
                callback.onChangeTheme();
    }
    public static int getCurrentThemeId() {
        return sCurrentThemeId;
    }
    interface ThemeChangeCallback {
        void onChangeTheme();
    }
}
