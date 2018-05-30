package com.github.sikv.photos.model

import android.os.Parcel
import android.os.Parcelable

data class Photo(
        val id: String,
        val width: Int,
        val height: Int,
        val color: String,
        val likes: Int,
        val description: String?,
        val user: User,
        val urls: Urls,
        val links: Links

) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readParcelable(User::class.java.classLoader),
            parcel.readParcelable(Urls::class.java.classLoader),
            parcel.readParcelable(Links::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeString(color)
        parcel.writeInt(likes)
        parcel.writeString(description)
        parcel.writeParcelable(user, flags)
        parcel.writeParcelable(urls, flags)
        parcel.writeParcelable(links, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Photo> {
        override fun createFromParcel(parcel: Parcel): Photo {
            return Photo(parcel)
        }

        override fun newArray(size: Int): Array<Photo?> {
            return arrayOfNulls(size)
        }
    }
}