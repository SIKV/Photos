package com.github.sikv.photos.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.sikv.photos.config.DbConfig
import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.model.Photo
import kotlinx.android.parcel.Parcelize

@Entity(tableName = DbConfig.favoritePhotosTableName)
@Parcelize
class FavoritePhotoEntity(
        @PrimaryKey
        var id: String = "",

        var previewUrl: String = "",
        var downloadUrl: String = "",
        var shareUrl: String = "",

        var photographerName: String = "",
        var photographerImageUrl: String? = "",
        var photographerUrl: String? = "",

        var source: PhotoSource = PhotoSource.UNSPECIFIED,

        // Sorting options
        val dateAdded: Long = System.currentTimeMillis(),

        // Remove/Undo operations
        val markedAsDeleted: Boolean = false
) : Photo() {

    companion object {
        fun fromPhoto(photo: Photo): FavoritePhotoEntity {
            return FavoritePhotoEntity(
                    id = photo.getPhotoId(),
                    previewUrl = photo.getPhotoPreviewUrl(),
                    downloadUrl = photo.getPhotoDownloadUrl(),
                    shareUrl = photo.getPhotoShareUrl(),
                    photographerName = photo.getPhotoPhotographerName(),
                    photographerImageUrl = photo.getPhotoPhotographerImageUrl(),
                    photographerUrl = photo.getPhotoPhotographerUrl(),
                    source = photo.getPhotoSource()
            )
        }
    }

    override fun getPhotoId(): String {
        return id
    }

    override fun getPhotoPreviewUrl(): String {
        return previewUrl
    }

    override fun getPhotoDownloadUrl(): String {
        return downloadUrl
    }

    override fun getPhotoShareUrl(): String {
        return shareUrl
    }

    override fun getPhotoPhotographerName(): String {
        return photographerName
    }

    override fun getPhotoPhotographerImageUrl(): String? {
        return photographerImageUrl
    }

    override fun getPhotoPhotographerUrl(): String? {
        return photographerUrl
    }

    override fun getPhotoSource(): PhotoSource {
        return source
    }

    override fun isLocalPhoto(): Boolean {
        return true
    }
}