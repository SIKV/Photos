package com.github.sikv.photos.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.sikv.photos.model.Photo
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "FavoritePhoto")
@Parcelize
class FavoritePhotoEntity(
        @PrimaryKey
        var id: String,
        var width: Int,
        var height: Int,
        var thumbUrl: String,
        var photographer: String,
        var originalSource: String
) : Photo() {

    companion object {
        fun fromPhoto(photo: Photo): FavoritePhotoEntity {
            return FavoritePhotoEntity(
                    photo.getPhotoId(),
                    photo.getPhotoWidth(),
                    photo.getPhotoHeight(),
                    photo.getThumbnailUrl(),
                    photo.getPhotographerName(),
                    photo.getSource()
            )
        }
    }

    override fun getPhotoId(): String {
        return id
    }

    override fun getPhotoWidth(): Int {
        return width
    }

    override fun getPhotoHeight(): Int {
        return height
    }

    override fun getThumbnailUrl(): String {
        return thumbUrl
    }

    override fun getPhotographerName(): String {
        return photographer
    }

    override fun getSource(): String {
        return originalSource
    }

    override fun isLocalPhoto(): Boolean {
        return true
    }
}