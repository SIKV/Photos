package com.github.sikv.photos.model

import android.os.Parcelable

interface Photo : Parcelable {

    fun getPhotoId(): String = ""
    fun getLargeUrl(): String = ""
    fun getNormalUrl(): String = ""
    fun getSmallUrl(): String = ""
    fun getShareUrl(): String = ""
    fun getPhotographerName(): String = ""
    fun getPhotographerUrl(): String? = null
    fun getSource(): String = ""
    fun getSourceUrl(): String = ""
    fun isLocalPhoto(): Boolean = false

    override fun equals(other: Any?): Boolean
}