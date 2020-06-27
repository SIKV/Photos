package com.github.sikv.photos.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.sikv.photos.model.Photo

class SetWallpaperViewModelFactory(
        private val application: Application,
        private val photo: Photo
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(modelClass)) {
            return SetWallpaperViewModel(application, photo) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}