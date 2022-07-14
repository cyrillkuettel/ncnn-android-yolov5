package com.tencent.ncnnyolox.pixelcopy;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class implements the callback after ImageProcessor as initialized the Copy process.
  */
public class PixelCopyCallback implements PostTake {
    private static final String TAG = "PixelCopyCallback";


    @Override
    public void onSuccess(Bitmap bitmap) {
        Log.d(TAG, "onSuccess!");
       saveBitmap(bitmap);
    }

    @Override
    public void onFailure(int error) {
        Log.e(TAG, "onFailure!");

    }

    public void saveBitmap(Bitmap bmp)  {
            final String filename = "test_image_pixel_copy.jpg";
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
            File f = new File(Environment.getExternalStorageDirectory()
                    + File.separator + filename);
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            Log.d(TAG, "failed to save bitmap in PixelCopyCallback." +
                    "Probably need to get Android permissions first. ");
            e.printStackTrace();
        }

    }
}
