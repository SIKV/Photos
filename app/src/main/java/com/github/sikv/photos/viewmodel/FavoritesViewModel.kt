package com.github.sikv.photos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.sikv.photos.App
import com.github.sikv.photos.data.Event
import com.github.sikv.photos.database.FavoritePhotoEntity
import com.github.sikv.photos.database.FavoritesDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoritesViewModel(
        application: Application
) : AndroidViewModel(application) {

    @Inject
    lateinit var favoritesDao: FavoritesDao

    var favoritesLiveData: LiveData<List<FavoritePhotoEntity>>
        private set

    private val favoritesDeleteAllMutableLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()

    var favoritesDeleteAllLiveData: LiveData<Event<Boolean>> = favoritesDeleteAllMutableLiveData

    private var deletedFavorites: List<FavoritePhotoEntity> = emptyList()

    init {
        App.instance.appComponent.inject(this)

        favoritesLiveData = favoritesDao.getAll()
    }

    fun deleteAll() {
        GlobalScope.launch {
           val count = favoritesDao.getCount()

           if (count > 0) {
               deletedFavorites = favoritesDao.getAllList()
               favoritesDao.deleteAll()

               favoritesDeleteAllMutableLiveData.postValue(Event(true))
           } else {
               favoritesDeleteAllMutableLiveData.postValue(Event(false))
           }
       }
    }

    fun undoDeleteAll() {
        GlobalScope.launch {
            deletedFavorites.forEach { photo ->
                favoritesDao.insert(photo)
            }

            deletedFavorites = emptyList()
        }
    }

    fun deleteAllForever() {
        deletedFavorites = emptyList()
    }
}