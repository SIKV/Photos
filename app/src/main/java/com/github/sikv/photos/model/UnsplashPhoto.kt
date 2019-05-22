package com.github.sikv.photos.model

import android.os.Parcel
import android.os.Parcelable


data class UnsplashPhoto(
        val id: String,
        val width: Int,
        val height: Int,
        val color: String,
        val likes: Int,
        val description: String?,
        val user: User,
        val urls: Urls,
        val links: Links

) : Photo {

    companion object CREATOR : Parcelable.Creator<UnsplashPhoto> {
        const val SOURCE = "Unsplash"

        override fun createFromParcel(parcel: Parcel): UnsplashPhoto {
            return UnsplashPhoto(parcel)
        }

        override fun newArray(size: Int): Array<UnsplashPhoto?> {
            return arrayOfNulls(size)
        }
    }

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

    override fun getPhotoId(): String {
        return id
    }

    override fun getLargeUrl(): String {
        return urls.full
    }

    override fun getNormalUrl(): String {
        return urls.regular
    }

    override fun getSmallUrl(): String {
        return urls.regular
    }

    override fun getShareUrl(): String {
        return links.html
    }

    override fun getPhotographerName(): String {
        return user.name
    }

    override fun getPhotographerUrl(): String? {
        return user.portfolioUrl
    }

    override fun getSource(): String {
        return SOURCE
    }

    override fun getSourceUrl(): String {
        return links.html
    }

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
}