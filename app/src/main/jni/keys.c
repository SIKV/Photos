#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_github_sikv_photos_Keys_getPexelsKey(JNIEnv *env, jobject instance) {
    return (*env)-> NewStringUTF(env, "_KEY_");
}

JNIEXPORT jstring JNICALL
Java_com_github_sikv_photos_Keys_getUnsplashKey(JNIEnv *env, jobject instance) {
    return (*env)-> NewStringUTF(env, "_KEY_");
}

JNIEXPORT jstring JNICALL
Java_com_github_sikv_photos_Keys_getPixabayKey(JNIEnv *env, jobject instance) {
    return (*env)-> NewStringUTF(env, "_KEY_");
}