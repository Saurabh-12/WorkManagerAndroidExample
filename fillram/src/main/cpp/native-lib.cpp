#include <jni.h>
#include <string>
#include <android/log.h>
#include <unistd.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_saurabhsharma123k_fillram_example_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


char *ptr = NULL;
extern "C" JNIEXPORT jint JNICALL
Java_com_saurabhsharma123k_fillram_example_MainActivity_sendData( JNIEnv* env, jobject, jint data, jint count)
{
        ptr = (char *) malloc((jlong) data);
        if(ptr == NULL) {
            __android_log_print(ANDROID_LOG_DEBUG, "Saurabh_Native", "Failed to allocate native memory  size : %d", data*count);
            return 0;
        }
        else{
            __android_log_print(ANDROID_LOG_DEBUG, "Saurabh_Native", "allocated native memory  size : %d", data*count);
            memset(ptr, '1', data);
            return 1;
        }

}


