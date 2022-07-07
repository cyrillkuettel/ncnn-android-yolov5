#ifndef YOLOX_H
#define YOLOX_H

#include <opencv2/core/core.hpp>

#include <net.h>

struct Object
{
    cv::Rect_<float> rect;
    int label;
    float prob;
};

extern JavaVM* javaVM_global;
extern jclass MainActivityClass;
extern jobject MainActivityObject;

class Yolox
{
public:
    Yolox();

    int load(const char* modeltype, int target_size, const float* mean_vals, const float* norm_vals, bool use_gpu = false);

    int load(AAssetManager* mgr, const char* modeltype, int target_size, bool use_gpu = false);

    int detect(const cv::Mat& rgb, std::vector<Object>& objects, float prob_threshold = 0.65f, float nms_threshold = 0.75f);

    int detect_yolov5(const cv::Mat& rgb, std::vector<Object>& objects, float prob_threshold = 0.65f, float nms_threshold = 0.75f);

    int draw_yolov5(cv::Mat& rgb, const std::vector<Object>& objects);

    int draw(cv::Mat& rgb, const std::vector<Object>& objects);

private:

    ncnn::Net yolox;

    int target_size;
    float mean_vals[3];
    float norm_vals[3];
    int image_w;
    int image_h;
    int in_w;
    int in_h;

    ncnn::UnlockedPoolAllocator blob_pool_allocator;
    ncnn::PoolAllocator workspace_pool_allocator;
};

#endif // YOLOX_H
