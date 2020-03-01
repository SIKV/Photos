package com.github.sikv.photos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.github.sikv.photos.App
import com.github.sikv.photos.enumeration.DownloadPhotoState
import com.github.sikv.photos.util.cancelPhotoDownloading
import com.github.sikv.photos.util.getSavedPhotoUri
import com.github.sikv.photos.util.startSetWallpaperActivity

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val downloadPhotoStateLiveData = App.instance.downloadPhotoStateLiveData
    val setWallpaperStateLiveData = App.instance.setWallpaperStateLiveData

    fun setWallpaper() {
        getApplication<Application>().getSavedPhotoUri()?.let { uri ->
            getApplication<Application>().startSetWallpaperActivity(uri)

        } ?: run {

            // TODO Handle
        }
    }

    fun cancelSetWallpaper() {
        when (downloadPhotoStateLiveData.value) {
            DownloadPhotoState.DOWNLOADING_PHOTO -> {
                getApplication<Application>().cancelPhotoDownloading()
            }

            else -> {
                App.instance.postDownloadPhotoStateLiveData(DownloadPhotoState.CANCELED)
            }
        }
    }
}