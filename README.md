
# Disclaimer
Credit for this project should go to: https://github.com/nihui. I merely made some slight adjustments.
# What's new
This repository implements a callback from the C++ Layer to the Android Layer using JNI (java native interface).  
This essentially means you can access the results (label and probability) of the object detection comfortably from the Java Layer, like shown in the following diagram:

![](docs/Android_Architecture_jni.png)
In code, this is implemented as a callback in `MainActivity`. This method is called from the native (C/C++) Layer. 
```Java
    public void callback(String output, String probability) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.textViewCurrentLabel.setText(String.format("label: %s, \nprobability: %s",
                        output, probability));
            }
        });
    }
```
# Demo
![](docs/177433672-4bb84c52-ac42-41ee-a5c3-0f1bbb02d4d5.png)
# ncnn-android-yolo5
This is a sample ncnn android project, it depends on ncnn library and opencv

https://github.com/Tencent/ncnn

https://github.com/nihui/opencv-mobile



## how to build and run
### step1
https://github.com/Tencent/ncnn/releases

* Download ncnn-YYYYMMDD-android-vulkan.zip or build ncnn for android yourself
* Extract ncnn-YYYYMMDD-android-vulkan.zip into **app/src/main/jni** and change the **ncnn_DIR** path to yours in **app/src/main/jni/CMakeLists.txt**

### step2
https://github.com/nihui/opencv-mobile

* Download opencv-mobile-XYZ-android.zip
* Extract opencv-mobile-XYZ-android.zip into **app/src/main/jni** and change the **OpenCV_DIR** path to yours in **app/src/main/jni/CMakeLists.txt**

