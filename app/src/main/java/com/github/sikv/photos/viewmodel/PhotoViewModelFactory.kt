package com.github.sikv.photos.viewmodel

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.github.sikv.photos.model.UnsplashPhoto

class PhotoViewModelFactory(
        private val application: Application,
        private val unsplashPhoto: UnsplashPhoto

) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(modelClass)) {
            return PhotoViewModel(application, unsplashPhoto) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}