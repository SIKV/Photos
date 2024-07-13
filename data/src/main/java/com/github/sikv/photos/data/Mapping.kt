import com.github.sikv.photos.data.PhotoDTO
import com.github.sikv.photos.data.PhotoSourceDTO
import com.github.sikv.photos.data.PhotosDTO
import com.github.sikv.photos.domain.PhotoData
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.domain.PhotoSource
import com.google.protobuf.StringValue

internal fun List<Photo>.toDTO(): PhotosDTO {
    return PhotosDTO
        .newBuilder()
        .addAllPhotos(map { photo -> photo.toDTO() })
        .build()
}

internal fun PhotosDTO.toDomain(): List<Photo> {
    return photosList.map { photo -> photo.toDomain() }
}

internal fun Photo.toDTO(): PhotoDTO {
    return PhotoDTO
        .newBuilder()
        .apply {
            setId(getPhotoId())
            setPreviewUrl(getPhotoPreviewUrl())
            setFullPreviewUrl(getPhotoFullPreviewUrl())
            setDownloadUrl(getPhotoDownloadUrl())
            setShareUrl(getPhotoShareUrl())
            setPhotographerName(getPhotoPhotographerName())
            getPhotoPhotographerImageUrl()?.let { setPhotographerImageUrl(StringValue.of(it)) }
            getPhotoPhotographerUrl()?.let { setPhotographerUrl(StringValue.of(it)) }
            setSource(getPhotoSource().toDTO())
        }
        .build()
}

internal fun PhotoDTO.toDomain(): Photo {
    return PhotoData(
        id = id,
        previewUrl = previewUrl,
        fullPreviewUrl = fullPreviewUrl,
        downloadUrl = downloadUrl,
        shareUrl = shareUrl,
        photographerName = photographerName,
        photographerImageUrl = photographerImageUrl.toDomain(),
        photographerUrl = photographerUrl.toDomain(),
        source = source.toDomain()
    )
}

internal fun PhotoSource.toDTO(): PhotoSourceDTO {
    return when (this) {
        PhotoSource.UNSPECIFIED -> PhotoSourceDTO.UNKNOWN
        PhotoSource.PEXELS -> PhotoSourceDTO.PEXELS
        PhotoSource.UNSPLASH -> PhotoSourceDTO.UNSPLASH
        PhotoSource.PIXABAY -> PhotoSourceDTO.PIXABAY
    }
}

internal fun PhotoSourceDTO.toDomain(): PhotoSource {
    return when (this) {
        PhotoSourceDTO.UNKNOWN -> PhotoSource.UNSPECIFIED
        PhotoSourceDTO.PEXELS -> PhotoSource.PEXELS
        PhotoSourceDTO.UNSPLASH -> PhotoSource.UNSPLASH
        PhotoSourceDTO.PIXABAY -> PhotoSource.PIXABAY
        PhotoSourceDTO.UNRECOGNIZED -> PhotoSource.UNSPECIFIED
    }
}

internal fun StringValue.toDomain(): String? {
    return value.takeIf { it.isNotEmpty() }
}
