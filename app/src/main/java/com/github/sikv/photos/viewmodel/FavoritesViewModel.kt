package com.github.sikv.photos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.sikv.photos.data.Event
import com.github.sikv.photos.database.FavoritePhotoEntity
import com.github.sikv.photos.database.FavoritesDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FavoritesViewModel(
        application: Application,
        private val favoritesDataSource: FavoritesDao

) : AndroidViewModel(application) {

    val favoritesLiveData: LiveData<List<FavoritePhotoEntity>> = favoritesDataSource.getAll()

    var favoritesDeleteEvent: MutableLiveData<Event<Boolean>>
        private set

    private var deletedFavorites: List<FavoritePhotoEntity> = emptyList()

    init {
        favoritesDeleteEvent = MutableLiveData()
    }

    fun deleteAll() {
        GlobalScope.launch {
           val count = favoritesDataSource.getCount()

           if (count > 0) {
               deletedFavorites = favoritesDataSource.getAllList()
               favoritesDataSource.deleteAll()

               favoritesDeleteEvent.postValue(Event(true))

           } else {
               favoritesDeleteEvent.postValue(Event(false))
           }
       }
    }

    fun undoDeleteAll() {
        GlobalScope.launch {
            deletedFavorites.forEach { photo ->
                favoritesDataSource.insert(photo)
            }

            deletedFavorites = emptyList()
        }
    }

    fun deleteAllForever() {
        deletedFavorites = emptyList()
    }
}