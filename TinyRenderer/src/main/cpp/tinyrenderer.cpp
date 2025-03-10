#include <jni.h>
#include <android/log.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <cstring> // 用于 memcpy
#include <string>
#include <vector>
#include <iostream>
#include <filesystem>
#include <thread>

#include "rasterizer.h"
#include "model.h"
#include "utils.h"

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define STB_IMAGE_WRITE_IMPLEMENTATION
#include "stb_image_write.h"

#define TAG "DUMPA1N"

int WIDTH = 700;
int HEIGHT = 700;

const std::string FilesDir = "/data/data/com.example.mixture/files/";

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_tinyrenderer_NativeLib_doRender(
        JNIEnv* env,
        jobject /* this */,
        jstring modelName) {

    Rasterizer rst(WIDTH, HEIGHT);

    rst.set_vertex_shader((void*)&default_vertex_shader);
    rst.set_fragment_shader((void*)&african_head_fragment_shader);

    // rst.set_texture(new Texture(FilesDir + "models/spot/spot_texture.png"));
    rst.add_texture("texture", new Texture(FilesDir + "models/african_head/african_head_SSS.jpg"));
    rst.add_texture("diffuse", new Texture(FilesDir + "models/african_head/african_head_diffuse.tga"));
    rst.add_texture("specular", new Texture(FilesDir + "models/african_head/african_head_spec.tga"));
    rst.add_texture("normal", new Texture(FilesDir + "models/african_head/african_head_nm_tangent.tga"));
    // rst.set_texture("diffuse", new Texture(FilesDir + "models/diablo3_pose/diablo3_pose_diffuse.tga"));

    Model* obj = new Model();
    // obj->load(FilesDir + "models/spot/spot_triangulated_good.obj");
    obj->load(FilesDir + "models/african_head/african_head.obj");
    // obj->load(FilesDir + "models/diablo3_pose/diablo3_pose.obj");

    std::vector<Triangle*> triangles;
    for (int i = 0; i < obj->v_indices.size(); i += 3) {
        Triangle* tri = new Triangle();
        for (int j = 0; j < 3; j++) {
            tri->setVertices(j, obj->vertices[obj->v_indices[i + j]]);
            tri->setTexCoords(j, obj->tex_coords[obj->vt_indices[i + j]]);
            tri->setNormals(j, obj->normals[obj->vn_indices[i + j]]);
        }
        triangles.push_back(tri);
    }

    while (1) {
        rst.clear_buffer();

        rst.draw(triangles);

        static auto ViewPort = [](Vector4f& p, int w, int h) -> void {
            float f1 = (50 - 0.1) / 2.0;
            float f2 = (50 + 0.1) / 2.0;
            p.x = w*0.5f*(p.x+1.0f);
            p.y = h*(1.0f - 0.5f*(p.y+1.0f));
            p.z = p.z * f1 + f2;
        };

        stbi_write_png((FilesDir + "out.png").c_str(), WIDTH, HEIGHT, 3, rst.get_stb_frame_buffer().data(), WIDTH * 3);
        break;
    }

    std::string hello = "doRender " + std::to_string(WIDTH * HEIGHT);

    return env->NewStringUTF(hello.c_str());
}


std::atomic<bool> bIsRendering(false); // 控制渲染是否继续

extern "C" JNIEXPORT void JNICALL
Java_com_example_tinyrenderer_NativeLib_startRender(
        JNIEnv* env,
        jobject /* this */,
        jobject surface,
        jstring modelName) {

    if (bIsRendering) return;

    bIsRendering = true;

    // 创建 ANativeWindow
    ANativeWindow* window = ANativeWindow_fromSurface(env, surface);
    if (!window) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "ANativeWindow_fromSurface failed");
        return;
    }

    // 设置缓冲区参数
    ANativeWindow_setBuffersGeometry(window, WIDTH, HEIGHT, WINDOW_FORMAT_RGBA_8888);

    const char* chars = env->GetStringUTFChars(modelName, nullptr);
    std::string modelname(chars);
    env->ReleaseStringUTFChars(modelName, chars);

    __android_log_print(ANDROID_LOG_INFO, TAG, "model name: %s", modelname.c_str());

    // **启动渲染线程**
    std::thread renderThread([window, modelname]() {
        Rasterizer rst(WIDTH, HEIGHT);
        Model obj;

        rst.set_vertex_shader((void*)&default_vertex_shader);

        if (modelname == "africa_head") {
            rst.set_fragment_shader((void*)&african_head_fragment_shader);
            rst.add_texture("texture", new Texture(FilesDir + "models/african_head/african_head_SSS.jpg"));
            rst.add_texture("diffuse", new Texture(FilesDir + "models/african_head/african_head_diffuse.tga"));
            rst.add_texture("specular", new Texture(FilesDir + "models/african_head/african_head_spec.tga"));
            rst.add_texture("normal", new Texture(FilesDir + "models/african_head/african_head_nm_tangent.tga"));
            obj.load(FilesDir + "models/african_head/african_head.obj");
        } else if (modelname == "spot") {
            rst.set_fragment_shader((void*)&phong_texture_fragment_shader);
            rst.set_texture(new Texture(FilesDir + "models/spot/spot_texture.png"));
            obj.load(FilesDir + "models/spot/spot_triangulated_good.obj");
        } else if (modelname == "diablo3_pose") {
            rst.set_fragment_shader((void*)&phong_texture_fragment_shader);
            rst.set_texture(new Texture(FilesDir + "models/diablo3_pose/diablo3_pose_diffuse.tga"));
            obj.load(FilesDir + "models/diablo3_pose/diablo3_pose.obj");
        }

        std::vector<Triangle*> triangles;
        for (int i = 0; i < obj.v_indices.size(); i += 3) {
            Triangle* tri = new Triangle();
            for (int j = 0; j < 3; j++) {
                tri->setVertices(j, obj.vertices[obj.v_indices[i + j]]);
                tri->setTexCoords(j, obj.tex_coords[obj.vt_indices[i + j]]);
                tri->setNormals(j, obj.normals[obj.vn_indices[i + j]]);
            }
            triangles.push_back(tri);
        }

        while (bIsRendering) {
            rst.clear_buffer();

            rst.draw(triangles);

            // 锁定缓冲区
            ANativeWindow_Buffer buffer;
            if (ANativeWindow_lock(window, &buffer, nullptr) != 0) {
                __android_log_print(ANDROID_LOG_ERROR, TAG, "ANativeWindow_lock failed");
                ANativeWindow_release(window);
                return;
            }

            uint8_t *dst = (uint8_t *) buffer.bits;

            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    int src_index = (y * WIDTH + x) * 3;  // RGB
                    int dst_index = (y * buffer.stride + x) * 4;  // RGBA

                    dst[dst_index + 0] = rst.stb_frame_buffer[src_index + 0]; // R
                    dst[dst_index + 1] = rst.stb_frame_buffer[src_index + 1]; // G
                    dst[dst_index + 2] = rst.stb_frame_buffer[src_index + 2]; // B
                    dst[dst_index + 3] = 255; // Alpha
                }
            }

            ANativeWindow_unlockAndPost(window);

            std::this_thread::sleep_for(std::chrono::milliseconds(33)); // 30 FPS
        }

        ANativeWindow_release(window);
    });

    renderThread.detach();
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_tinyrenderer_NativeLib_stopRender(JNIEnv* env, jobject /* this */) {
    bIsRendering = false;
}

extern "C" JNIEXPORT int JNICALL
Java_com_example_tinyrenderer_NativeLib_isRendering(JNIEnv* env, jobject /* this */) {
    return bIsRendering;
}
