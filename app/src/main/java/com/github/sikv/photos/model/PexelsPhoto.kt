package com.github.sikv.photos.model

import android.os.Parcel
import android.os.Parcelable

data class PexelsPhoto(
        val width: Int,
        val height: Int,
        val url: String,
        val photographer: String,
        val src: Src

) : Photo {

    companion object CREATOR : Parcelable.Creator<PexelsPhoto> {
        const val SOURCE = "Pexels"

        override fun createFromParcel(parcel: Parcel): PexelsPhoto {
            return PexelsPhoto(parcel)
        }

        override fun newArray(size: Int): Array<PexelsPhoto?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(Src::class.java.classLoader))

    override fun getPhotoId(): String {
        return url.substring(url.substring(0, url.length - 1).lastIndexOf("-") + 1, url.lastIndexOf("/"))
    }

    override fun getLargeUrl(): String {
        return src.large2x
    }

    override fun getNormalUrl(): String {
        return src.large
    }

    override fun getSmallUrl(): String {
        return src.large
    }

    override fun getShareUrl(): String {
        return url
    }

    override fun getPhotographerName(): String {
        return photographer
    }

    override fun getPhotographerUrl(): String? {
        return null
    }

    override fun getSource(): String {
        return SOURCE
    }

    override fun getSourceUrl(): String {
        return url
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeString(url)
        parcel.writeString(photographer)
        parcel.writeParcelable(src, flags)
    }

    override fun describeContents(): Int {
        return 0
    }
}