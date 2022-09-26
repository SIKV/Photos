#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_github_sikv_photos_api_Secrets_getPexelsKey(JNIEnv *env, jobject instance) {
    return (*env)-> NewStringUTF(env, "_KEY_");
}

JNIEXPORT jstring JNICALL
Java_com_github_sikv_photos_api_Secrets_getUnsplashKey(JNIEnv *env, jobject instance) {
    return (*env)-> NewStringUTF(env, "_KEY_");
}

JNIEXPORT jstring JNICALL
Java_com_github_sikv_photos_api_Secrets_getPixabayKey(JNIEnv *env, jobject instance) {
    return (*env)-> NewStringUTF(env, "_KEY_");
}
