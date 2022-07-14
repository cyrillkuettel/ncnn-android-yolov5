package com.tencent.ncnnyolox.image;

import android.graphics.Bitmap;

/**
 * I copy image data from the SurfaceView cameraView. Copying this data is actually not a minor operation.
 * I use android.view.PixelCopy to copy image data. PixelCopy requires to attach a callback to the
 * listener. The listener is defined in MainActivityQRCodeNCNN.
 */
public interface PostTake {

    /**
     * Most of the time, you want to do something with the Image data, probably save to disk.
     * Here I will send the data to the website.
     * @param bitmap the Bitmap we copied from the screen.
     */
    void onSuccess(Bitmap bitmap);

    /**
     * The attempt to copy failed. Something has gone very wrong.
     */
    void onFailure(int error);
}