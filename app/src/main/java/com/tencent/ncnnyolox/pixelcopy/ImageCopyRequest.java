package com.tencent.ncnnyolox.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.PixelCopy;
import android.view.SurfaceView;

/**
 * This is a class to copy the contents of SurfaceView.
 * It makes use of PixelCopyCallback.
 * Usage: ImageCopyRequest copyRequest = new ImageCopyRequest(surfaceView);
 * copyRequest.start();
 */
public class ImageCopyRequest implements ImageProcessor{
    private static final String TAG = "ImageCopyRequest";
    final SurfaceView cameraview;
    PixelCopyCallback pixelCopyCallback;
    boolean setHasPermissionToSave;

    public ImageCopyRequest(SurfaceView camerview) {
        cameraview = camerview;

    }

    @Override
    public void start() {
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

        // startCopyOnLockAcquired(cameraview);

    }
    @Override
    public void copyBitmapAndAttachListener(SurfaceView view, PostTake callback) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);

        PixelCopy.OnPixelCopyFinishedListener listener =
                new PixelCopy.OnPixelCopyFinishedListener() {
                    @Override
                    public void onPixelCopyFinished(int copyResult) {
                        if (copyResult == PixelCopy.SUCCESS) {
                            callback.onSuccess(bitmap);
                        } else {
                            callback.onFailure(copyResult);
                        }
                    }
                };
        try {
            Log.d(TAG, "Trying PixelCopy.request");
            PixelCopy.request(view, bitmap, listener, new Handler());
        } catch (Exception e) {
            Log.e(TAG, "failed: PixelCopy.request(view, bitmap, listener, new Handler());");
            e.printStackTrace();
        }
    }

    public void startCopyOnLockAcquired(SurfaceView cameraView) {
        Canvas canvas = null;
        int count = 0;
        while (canvas == null || count > 100) { // very dubious of course, TODO.need to fix this
            try {
                canvas = cameraView.getHolder().lockCanvas(); // maybe try hardware lock
                Log.v(TAG, "onclick!");
                if (canvas != null) {
                    Log.d(TAG, String.format("Got Lock on Canvas after %d tries", count));
                    copyBitmapAndAttachListener(cameraView, pixelCopyCallback);
                    cameraView.getHolder().unlockCanvasAndPost(canvas);
                } else {
                    Log.v(TAG, "canvas == null");
                }
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Canvas is locked, try again.");
                e.printStackTrace();
            }
            count++;
        }
    }

    @Override
    public void setHasPermissionToSave(boolean value) {
        this.setHasPermissionToSave = value;
    }


}
