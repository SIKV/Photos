package com.github.sikv.photos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.github.sikv.photos.App
import com.github.sikv.photos.database.FavoritePhotoEntity
import com.github.sikv.photos.database.FavoritesDao
import com.github.sikv.photos.manager.FavoritesManager
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.Event
import javax.inject.Inject

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var favoritesDao: FavoritesDao

    @Inject
    lateinit var favoritesManager: FavoritesManager

    val favoritesLiveData: LiveData<List<FavoritePhotoEntity>>
    val deleteAllEvent: LiveData<Event<Boolean>>

    init {
        App.instance.appComponent.inject(this)

        favoritesLiveData = favoritesDao.getAll()
        deleteAllEvent = favoritesManager.deleteAllEvent
    }

    fun invertFavorite(photo: Photo) {
        favoritesManager.invertFavorite(photo)
    }

    fun deleteAll() {
        favoritesManager.deleteAll()
    }

    fun undoDeleteAll() {
        favoritesManager.undoDeleteAll()
    }

    fun deleteAllFinally() {
        favoritesManager.deleteAllFinally()
    }
}