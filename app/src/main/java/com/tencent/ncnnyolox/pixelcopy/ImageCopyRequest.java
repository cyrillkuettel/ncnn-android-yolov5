package com.tencent.ncnnyolox.pixelcopy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.PixelCopy;
import android.view.SurfaceView;

/**
 * This is a class to copy the contents of SurfaceView.
 * It makes use of PixelCopyCallback.
 * Use Like this: ImageCopyRequest copyRequest = new ImageCopyRequest(surfaceView);
 * copyRequest.start();
 */
public class ImageCopyRequest {

    private static final String TAG = "ImageCopyRequest";
    final SurfaceView cameraview;
    PixelCopyCallback pixelCopyCallback;
    boolean setHasPermissionToSave;

    public ImageCopyRequest(SurfaceView camerview) {
        cameraview = camerview;
    }

    public void start() {
        Log.d(TAG, " Starting ImageCopyRequest");

        if (!setHasPermissionToSave) {
            Log.e(TAG, "Fatal: Image copy initialization started before we know for sure we have permissions. ");
            return;
        }
        pixelCopyCallback  = new PixelCopyCallback();
        try {
            copyBitmapAndAttachListener(cameraview, pixelCopyCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void copyBitmapAndAttachListener(SurfaceView view, PostTake callback) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);

        PixelCopy.OnPixelCopyFinishedListener listener =
                copyResult -> {
                    if (copyResult == PixelCopy.SUCCESS) {
                        callback.onSuccess(bitmap);
                    } else {
                        callback.onFailure(copyResult);
                    }
                };
        try {
            Log.d(TAG, "Starting PixelCopy");
            PixelCopy.request(view, bitmap, listener, new Handler());
        } catch (Exception e) {
            Log.e(TAG, "failed: PixelCopy.request(view, bitmap, listener, new Handler());");
            e.printStackTrace();
        }
    }


    public void setHasPermissionToSave(boolean value) {
        this.setHasPermissionToSave = value;
    }


}
