package com.github.sikv.photos.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.github.sikv.photos.model.Photo


@Entity(tableName = "PhotoData")
data class PhotoData(
        @PrimaryKey
        var id: String,
        var url: String,
        var originalSource: String

) : Photo {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    constructor(): this("", "", "")

    override fun getPhotoId(): String {
        return id
    }

    override fun getSmallUrl(): String {
        return url
    }

    override fun getSource(): String {
        return originalSource
    }

    override fun isLocalPhoto(): Boolean {
        return true
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(url)
        parcel.writeString(originalSource)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PhotoData> {
        override fun createFromParcel(parcel: Parcel): PhotoData {
            return PhotoData(parcel)
        }

        override fun newArray(size: Int): Array<PhotoData?> {
            return arrayOfNulls(size)
        }
    }
}