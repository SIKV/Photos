package com.github.sikv.photos.model

import android.os.Parcel
import android.os.Parcelable

data class Src(
        val original: String,
        val large: String,
        val large2x: String,
        val medium: String,
        val small: String,
        val portrait: String,
        val landscape: String,
        val tiny: String

) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(original)
        parcel.writeString(large)
        parcel.writeString(large2x)
        parcel.writeString(medium)
        parcel.writeString(small)
        parcel.writeString(portrait)
        parcel.writeString(landscape)
        parcel.writeString(tiny)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Src> {
        override fun createFromParcel(parcel: Parcel): Src {
            return Src(parcel)
        }

        override fun newArray(size: Int): Array<Src?> {
            return arrayOfNulls(size)
        }
    }
}