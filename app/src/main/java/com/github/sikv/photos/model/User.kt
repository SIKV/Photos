package com.github.sikv.photos.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class User(
        val username: String,
        val name: String,

        @SerializedName("portfolio_url")
        val portfolioUrl: String?,

        @SerializedName("profile_image")
        val profileImage: ProfileImage?

) : Parcelable {

        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readParcelable(ProfileImage::class.java.classLoader))

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(username)
                parcel.writeString(name)
                parcel.writeString(portfolioUrl)
                parcel.writeParcelable(profileImage, flags)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<User> {
                override fun createFromParcel(parcel: Parcel): User {
                        return User(parcel)
                }

                override fun newArray(size: Int): Array<User?> {
                        return arrayOfNulls(size)
                }
        }
}