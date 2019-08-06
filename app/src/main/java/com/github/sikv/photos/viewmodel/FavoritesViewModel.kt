package com.github.sikv.photos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.sikv.photos.Event
import com.github.sikv.photos.database.FavoritesDatabase
import com.github.sikv.photos.database.PhotoData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val favoritesDatabase: FavoritesDatabase

    val favoritesLiveData: LiveData<List<PhotoData>>

    var favoritesDeleteEvent: MutableLiveData<Event<Boolean>>
        private set

    private var deletedFavorites: List<PhotoData> = emptyList()

    init {
        favoritesDatabase = FavoritesDatabase.getInstance(getApplication())

        favoritesLiveData = favoritesDatabase.photoDao().getAll()

        favoritesDeleteEvent = MutableLiveData()
    }

    fun deleteAll() {
        GlobalScope.launch {
           val count = favoritesDatabase.photoDao().getCount()

           if (count > 0) {
               deletedFavorites = favoritesDatabase.photoDao().getAllList()
               favoritesDatabase.photoDao().deleteAll()

               favoritesDeleteEvent.postValue(Event(true))

           } else {
               favoritesDeleteEvent.postValue(Event(false))
           }
       }
    }

    fun undoDeleteAll() {
        GlobalScope.launch {
            deletedFavorites.forEach { photo ->
                favoritesDatabase.photoDao().insert(photo)
            }

            deletedFavorites = emptyList()
        }
    }

    fun deleteAllForever() {
        deletedFavorites = emptyList()
    }
}