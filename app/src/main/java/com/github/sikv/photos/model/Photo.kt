package com.github.sikv.photos.model

import android.content.Context

interface Photo {

    fun getPhotoId(): String
    fun getNormalUrl(): String
    fun getSmallUrl(): String
    fun getShareUrl(): String
    fun getPhotographerName(): String
    fun getSource(context: Context): String
}