package com.github.sikv.photos.model

import android.os.Parcelable

interface Photo : Parcelable {

    fun getPhotoId(): String
    fun getNormalUrl(): String
    fun getSmallUrl(): String
    fun getShareUrl(): String
    fun getPhotographerName(): String
    fun getPhotographerUrl(): String?
    fun getSource(): String
    fun getSourceUrl(): String
}