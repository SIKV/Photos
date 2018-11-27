package com.github.sikv.photos.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.github.sikv.photos.database.FavoritesDatabase
import com.github.sikv.photos.database.PhotoData

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val favoritesDatabase: FavoritesDatabase

    val favoritesLiveData: LiveData<List<PhotoData>>

    init {
        favoritesDatabase = FavoritesDatabase.getInstance(getApplication())

        favoritesLiveData = favoritesDatabase.photoDao().getAll()
    }
}