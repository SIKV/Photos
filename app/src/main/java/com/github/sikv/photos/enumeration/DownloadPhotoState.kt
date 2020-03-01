package com.github.sikv.photos.enumeration

enum class DownloadPhotoState {
    DOWNLOADING_PHOTO,
    PHOTO_READY,
    ERROR_DOWNLOADING_PHOTO,
    CANCELING,
    CANCELED
}