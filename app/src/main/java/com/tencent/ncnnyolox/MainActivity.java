// Tencent is pleased to support the open source community by making ncnn available.
//
// Copyright (C) 2021 THL A29 Limited, a Tencent company. All rights reserved.
//
// Licensed under the BSD 3-Clause License (the "License"); you may not use this file except
// in compliance with the License. You may obtain a copy of the License at
//
// https://opensource.org/licenses/BSD-3-Clause
//
// Unless required by applicable law or agreed to in writing, software distributed
// under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.

package com.tencent.ncnnyolox;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.tencent.ncnnyolox.databinding.ActivityMainBinding;
import com.tencent.ncnnyolox.pixelcopy.ImageCopyRequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback
{
    public static final int REQUEST_CAMERA = 100; //  random
    private static final String TAG = "MainActivity";
    public static final int REQUEST_READ_WRITE_EXTERNAL_STORAGE = 112;  //  random
    private NcnnYolox ncnnyolox = new NcnnYolox();
    private ActivityMainBinding binding;

    private int facing = 0;
    Context mContext = MainActivity.this;
    private Spinner spinnerModel;
    private Spinner spinnerCPUGPU;
    private int current_model = 0;
    private int current_cpugpu = 0;

    private SurfaceView cameraView;
    private ImageCopyRequest imageCopyRequest;



    public static final String[] EXTERNAL_STORAGE_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     ╔═══════════╤═════╗      Object detected     ╔═════════════════════╤═════╗
     ║ C++ Layer │ NDK ║------------------------->║ Android / JVM Layer │ SDK ║
     ╚═══════════╧═════╝                          ╚═════════════════════╧═════╝
     * */
    public void callback(String output, String probability, String x, String y, String width, String height) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.textViewCurrentLabel.setText(String.format("label: %s, \nprobability: %s, Rect: [x: %s, y: %s, width: %s, height: %s ]",
                        output, probability, x, y, width, height));
            }
        });
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        cameraView = (SurfaceView) findViewById(R.id.cameraview);

        cameraView.getHolder().setFormat(PixelFormat.RGBA_8888);
        cameraView.getHolder().addCallback(this);
        ncnnyolox.injectObjectReference(this); /** Get a reference to the Object MainActivity*/

        imageCopyRequest = new ImageCopyRequest(cameraView);

        if (!hasPermissions(mContext, EXTERNAL_STORAGE_PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, EXTERNAL_STORAGE_PERMISSIONS,
                    REQUEST_READ_WRITE_EXTERNAL_STORAGE);

            Log.d(TAG, "Requesting permissions");

        } else {
            Log.d(TAG, "Ok: READ_EXTERNAL_STORAGE permission already granted");
            imageCopyRequest.setHasPermissionToSave(true);
        }

        binding.buttonCropImage.setOnClickListener(arg0 -> {
            imageCopyRequest.start();
        });

        Button buttonSwitchCamera = (Button) findViewById(R.id.buttonSwitchCamera);
        buttonSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int new_facing = 1 - facing;
                ncnnyolox.closeCamera();
                ncnnyolox.openCamera(new_facing);
                facing = new_facing;
            }
        });

        spinnerModel = (Spinner) findViewById(R.id.spinnerModel);
        spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)
            {
                if (position != current_model)
                {
                    current_model = position;
                    reload();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });

        spinnerCPUGPU = (Spinner) findViewById(R.id.spinnerCPUGPU);
        spinnerCPUGPU.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)
            {
                if (position != current_cpugpu)
                {
                    current_cpugpu = position;
                    reload();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });

        reload();
    }


    private void reload()
    {
        boolean ret_init = ncnnyolox.loadModel(getAssets(), current_model, current_cpugpu);
        if (!ret_init)
        {
            Log.e("MainActivity", "ncnnyolox loadModel failed");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        ncnnyolox.setOutputWindow(holder.getSurface());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
       ncnnyolox.openCamera(facing);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        ncnnyolox.closeCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_READ_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 0) {
                Log.e(TAG, "grantResults.length == 0");

            }
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imageCopyRequest.setHasPermissionToSave(true);
                Log.d(TAG, "Great! we now have permissions for storage.");

            } else {
                imageCopyRequest.setHasPermissionToSave(false);
                Log.e(TAG, "The app was not allowed to write  storage.");

            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
