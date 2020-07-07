package com.github.sikv.photos.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.sikv.photos.model.Photo
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "FavoritePhoto")
@Parcelize
class FavoritePhotoEntity(
        @PrimaryKey
        var id: String = "",
        var width: Int = 0,
        var height: Int = 0,
        var createdAt: Long? = null,
        var description: String? = null,

        var previewUrl: String = "",
        var downloadUrl: String = "",
        var shareUrl: String = "",

        var photographerName: String = "",
        var photographerImageUrl: String? = "",
        var photographerUrl: String? = "",
        var source: String = "",

        // Sorting options
        val dateAdded: Long = System.currentTimeMillis(),

        // Remove/Undo operations
        val markedAsDeleted: Boolean = false
) : Photo() {

    companion object {
        fun fromPhoto(photo: Photo): FavoritePhotoEntity {
            return FavoritePhotoEntity(
                    id = photo.getPhotoId(),
                    width = photo.getPhotoWidth(),
                    height = photo.getPhotoHeight(),
                    createdAt = photo.getPhotoCreatedAt(),
                    description = photo.getPhotoDescription(),
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

    override fun getPhotoWidth(): Int {
        return width
    }

    override fun getPhotoHeight(): Int {
        return height
    }

    override fun getPhotoCreatedAt(): Long? {
        return createdAt
    }

    override fun getPhotoDescription(): String? {
        return description
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

    override fun getPhotoSource(): String {
        return source
    }

    override fun isLocalPhoto(): Boolean {
        return true
    }
}