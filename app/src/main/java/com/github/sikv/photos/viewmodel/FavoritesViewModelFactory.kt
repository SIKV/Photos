package com.github.sikv.photos.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.sikv.photos.database.FavoritesDao

class FavoritesViewModelFactory(
        private val application: Application,
        private val favoritesDataSource: FavoritesDao

) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(modelClass)) {
            return FavoritesViewModel(application, favoritesDataSource) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}