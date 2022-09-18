package com.github.sikv.photos.api

object Secrets {

    init {
        System.loadLibrary("keys")
    }

    external fun getPexelsKey(): String?
    external fun getUnsplashKey(): String?
    external fun getPixabayKey(): String?
}