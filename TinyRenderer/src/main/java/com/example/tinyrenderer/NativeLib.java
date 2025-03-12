package com.example.tinyrenderer;

import android.view.Surface;

public class NativeLib {

    /**
     * A native method that is implemented by the 'tinyrenderer' native library,
     * which is packaged with this application.
     */

    public native void startRender(Surface surface, String modelName);
    public native void stopRender(Surface surface);
    public native int isRendering();

    static {
        // Used to load the 'tinyrenderer' library on application startup.
        System.loadLibrary("tinyrenderer");
    }
}
