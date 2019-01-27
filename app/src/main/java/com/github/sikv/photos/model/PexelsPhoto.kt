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

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(Src::class.java.classLoader))

    override fun getPhotoId(): String {
        return url
    }

    override fun getNormalUrl(): String {
        return src.medium
    }

    override fun getSmallUrl(): String {
        return src.medium
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
        return "Pexels"
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

    companion object CREATOR : Parcelable.Creator<PexelsPhoto> {
        override fun createFromParcel(parcel: Parcel): PexelsPhoto {
            return PexelsPhoto(parcel)
        }

        override fun newArray(size: Int): Array<PexelsPhoto?> {
            return arrayOfNulls(size)
        }
    }
}