package com.github.sikv.photos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.sikv.photos.App
import com.github.sikv.photos.data.FavoritesRepository
import com.github.sikv.photos.database.FavoritePhotoEntity
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.AccountManager
import com.github.sikv.photos.util.Event
import com.github.sikv.photos.util.LoginStatus
import javax.inject.Inject

class FavoritesViewModel(application: Application) : AndroidViewModel(application), AccountManager.Callback {

    @Inject
    lateinit var accountManager: AccountManager

    @Inject
    lateinit var favoritesRepository: FavoritesRepository

    val favoritesLiveData: LiveData<List<FavoritePhotoEntity>>

    private val deleteAllMutableEvent = MutableLiveData<Event<Boolean>>()
    val deleteAllEvent: LiveData<Event<Boolean>> = deleteAllMutableEvent

    private val localStorageWaringVisibilityMutableLiveData = MutableLiveData<Boolean>()
    val localStorageWaringVisibilityLiveData: LiveData<Boolean> = localStorageWaringVisibilityMutableLiveData

    init {
        App.instance.appComponent.inject(this)

        favoritesLiveData = favoritesRepository.favoritesLiveData

        accountManager.subscribe(this)

        // If a user is not logged in show Local Storage warning
        localStorageWaringVisibilityMutableLiveData.postValue(accountManager.loginStatus == LoginStatus.LOGGED_OUT)
    }

    override fun onCleared() {
        super.onCleared()

        accountManager.unsubscribe(this)
    }

    override fun onLoginStatusChanged(status: LoginStatus) {
        when (status) {
            LoginStatus.LOGGED_IN -> {
                // Hide Local Storage warning when a user has been logged in
                localStorageWaringVisibilityMutableLiveData.postValue(false)
            }

            LoginStatus.LOGGED_OUT -> {
                // Show Local Storage warning when a user has been logged out
                localStorageWaringVisibilityMutableLiveData.postValue(true)
            }
        }
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