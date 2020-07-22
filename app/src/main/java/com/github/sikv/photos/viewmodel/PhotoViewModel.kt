package com.github.sikv.photos.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.event.Event
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.createShareIntent
import com.github.sikv.photos.ui.dialog.SetWallpaperDialog
import com.github.sikv.photos.util.downloadPhotoAndSaveToPictures
import com.github.sikv.photos.util.openUrl
import kotlinx.coroutines.launch
import javax.inject.Inject

class PhotoViewModel(
        application: Application,
        private var photo: Photo
) : AndroidViewModel(application), FavoritesRepository.Listener {

    @Inject
    lateinit var favoritesRepository: FavoritesRepository

    @Inject
    lateinit var glide: RequestManager

    private val showPhotoInfoMutableEvent = MutableLiveData<Event<Photo?>>()
    val showPhotoInfoEvent: LiveData<Event<Photo?>> = showPhotoInfoMutableEvent

    private val showPhotoMutableEvent = MutableLiveData<Event<Bitmap>>()
    val showPhotoEvent: LiveData<Event<Bitmap>> = showPhotoMutableEvent

    private val favoriteInitMutableEvent = MutableLiveData<Event<Boolean>>()
    val favoriteInitEvent: LiveData<Event<Boolean>> = favoriteInitMutableEvent

    private val favoriteChangedMutableLiveData = MutableLiveData<Boolean>()
    val favoriteChangedLiveData: LiveData<Boolean> = favoriteChangedMutableLiveData

    init {
        App.instance.appComponent.inject(this)

        viewModelScope.launch {
            /** Don't use FavoritesRepository.isFavorite(Photo) here because that method is using Photo.favorite flag.
             * Photo.favorite flag will be always false after using parcelable. */
            favoriteInitMutableEvent.value = Event(favoritesRepository.isFavoriteFromDatabase(photo))
        }

        favoritesRepository.subscribe(this)

        loadPhoto()
    }

    override fun onCleared() {
        super.onCleared()

        favoritesRepository.unsubscribe(this)
    }

    override fun onFavoriteChanged(photo: Photo, favorite: Boolean) {
        favoriteChangedMutableLiveData.postValue(favorite)
    }

    override fun onFavoritesChanged() {
        // Don't need to handle it
    }

    private fun loadPhoto() {
        var fullPhotoAlreadyLoaded = false

        if (photo.isLocalPhoto()) {
            loadLocalPhoto()
        } else {
            glide.asBitmap()
                    .load(photo.getPhotoFullPreviewUrl())
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onLoadStarted(placeholder: Drawable?) {
                            glide.asBitmap()
                                    .load(photo.getPhotoPreviewUrl())
                                    .into(object : CustomTarget<Bitmap>() {
                                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                                            if (!fullPhotoAlreadyLoaded) {
                                                showPhotoMutableEvent.value = Event(bitmap)
                                            }
                                        }

                                        override fun onLoadCleared(placeholder: Drawable?) { }
                                    })
                        }

                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                            showPhotoMutableEvent.value = Event(bitmap)
                            fullPhotoAlreadyLoaded = true
                        }

                        override fun onLoadCleared(placeholder: Drawable?) { }
                    })

            showPhotoInfoMutableEvent.postValue(Event(photo))
        }
    }

    private fun loadLocalPhoto() {
        glide.asBitmap()
                .load(photo.getPhotoPreviewUrl())
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        showPhotoMutableEvent.value = Event(bitmap)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) { }
                })

        viewModelScope.launch {
            try {
                when (photo.getPhotoSource()) {
                    PhotoSource.PEXELS -> ApiClient.INSTANCE.pexelsClient.getPhoto(photo.getPhotoId())
                    PhotoSource.UNSPLASH -> ApiClient.INSTANCE.unsplashClient.getPhoto(photo.getPhotoId())
                    else -> null
                }?.let { photo ->
                    this@PhotoViewModel.photo = photo

                    glide.asBitmap()
                            .load(photo.getPhotoFullPreviewUrl())
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                                    showPhotoMutableEvent.value = Event(bitmap)
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {}
                            })

                    showPhotoInfoMutableEvent.postValue(Event(photo))

                } ?: run {
                    // TODO Error loading photo
                }

            } catch (e: Exception) {
                // TODO Error loading photo
            }
        }
    }

    fun setWallpaper(fragmentManager: FragmentManager) {
        SetWallpaperDialog.newInstance(photo).show(fragmentManager)
    }

    fun downloadPhotoAndSave() {
        getApplication<Application>().apply {
            applicationContext.downloadPhotoAndSaveToPictures(photo.getPhotoDownloadUrl())

            App.instance.postGlobalMessage(getString(R.string.downloading_photo))
        }
    }

    fun invertFavorite() {
        favoritesRepository.invertFavorite(photo)
    }

    fun createShareIntent(): Intent {
        return photo.createShareIntent()
    }

    fun openAuthorUrl() {
        photo.getPhotoPhotographerUrl()?.let {
            (getApplication() as? Context)?.openUrl(it)
        }
    }

    fun openPhotoSource() {
        (getApplication() as? Context)?.openUrl(photo.getPhotoShareUrl())
    }
}