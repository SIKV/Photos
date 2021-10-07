package com.github.sikv.photos

object Keys {

    init {
        System.loadLibrary("keys")
    }

    external fun getPexelsKey(): String?
    external fun getUnsplashKey(): String?
    external fun getPixabayKey(): String?
}