package com.github.sikv.photos.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.sikv.photos.model.Photo
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "FavoritePhoto")
@Parcelize
data class FavoritePhotoEntity(
        @PrimaryKey
        var id: String,

        var url: String,
        var originalSource: String
) : Photo {

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
}