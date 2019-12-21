package com.github.sikv.photos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.github.sikv.photos.App
import com.github.sikv.photos.util.DownloadPhotoState
import com.github.sikv.photos.manager.PhotoManager
import javax.inject.Inject

class MainViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var photoManager: PhotoManager

    init {
        App.instance.appComponent.inject(this)
    }

    val downloadPhotoStateLiveData = App.instance.downloadPhotoStateLiveData
    val setWallpaperStateLiveData = App.instance.setWallpaperStateLiveData

    fun setWallpaper() {
        photoManager.getSavedPhotoUri(getApplication())?.let { uri ->
            photoManager.startSetWallpaper(getApplication(), uri)
        } ?: run {

            // TODO Handle
        }
    }

    fun cancelSetWallpaper() {
        when (downloadPhotoStateLiveData.value) {
            DownloadPhotoState.DOWNLOADING_PHOTO -> {
                photoManager.cancelDownloading(getApplication())
            }

            else -> {
                App.instance.postDownloadPhotoStateLiveData(DownloadPhotoState.CANCELED)
            }
        }
    }
}