package com.github.sikv.photos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.github.sikv.photos.App
import com.github.sikv.photos.enumeration.DownloadPhotoState
import com.github.sikv.photos.util.cancelPhotoDownloading
import com.github.sikv.photos.util.getSavedPhotoUri
import com.github.sikv.photos.util.startSetWallpaperActivity

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val downloadPhotoStateChangedLiveData = App.instance.downloadPhotoStateChangedLiveData
    val setWallpaperStateChangedEvent = App.instance.setWallpaperStateChangedEvent

    fun setWallpaper() {
        getApplication<Application>().getSavedPhotoUri()?.let { uri ->
            getApplication<Application>().startSetWallpaperActivity(uri)
        } ?: run {
            // TODO Handle
        }
    }

    fun cancelSetWallpaper() {
        when (downloadPhotoStateChangedLiveData.value) {
            DownloadPhotoState.DOWNLOADING_PHOTO -> {
                getApplication<Application>().cancelPhotoDownloading()
            }

            else -> {
                App.instance.postDownloadPhotoState(DownloadPhotoState.CANCELED)
            }
        }
    }
}