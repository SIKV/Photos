package com.github.sikv.photos.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.github.sikv.photos.model.Photo

class PhotoViewModelFactory(
        private val photo: Photo

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoViewModel::class.java)) {
            return PhotoViewModel(photo) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}