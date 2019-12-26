package com.github.sikv.photos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.sikv.photos.App
import com.github.sikv.photos.database.FavoritePhotoEntity
import com.github.sikv.photos.data.FavoritesRepository
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.Event
import javax.inject.Inject

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var favoritesRepository: FavoritesRepository

    val favoritesLiveData: LiveData<List<FavoritePhotoEntity>>

    private val deleteAllMutableEvent: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val deleteAllEvent: LiveData<Event<Boolean>> = deleteAllMutableEvent

    init {
        App.instance.appComponent.inject(this)

        favoritesLiveData = favoritesRepository.favoritesLiveData
    }

    fun invertFavorite(photo: Photo) {
        favoritesRepository.invertFavorite(photo)
    }

    fun deleteAll() {
        favoritesRepository.deleteAll {
            deleteAllMutableEvent.postValue(Event(it))
        }
    }

    fun undoDeleteAll() {
        favoritesRepository.undoDeleteAll()
    }

    fun deleteAllFinally() {
        favoritesRepository.deleteAllFinally()
    }
}