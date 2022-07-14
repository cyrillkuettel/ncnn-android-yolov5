package com.tencent.ncnnyolox.pixelcopy;

import android.graphics.Bitmap;

/**
 * I copy image data from the SurfaceView cameraView.
 * PixelCopy requires to attach a callback to the listener.
 */
public interface PostTake {

    /**
     * Most of the time, you want to do something with the Image data, probably save to disk.
     * @param bitmap the Bitmap we copied from the screen.
     */
    void onSuccess(Bitmap bitmap);

    /**
     * The attempt to copy failed.
     */
    void onFailure(int error);
}