package com.github.sikv.photos.viewmodel

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.event.Event
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.createShareIntent
import com.github.sikv.photos.ui.dialog.SetWallpaperDialog
import com.github.sikv.photos.util.downloadPhotoAndSaveToPictures
import com.github.sikv.photos.util.openUrl
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
        savedStateHandle: SavedStateHandle,
        private val photosRepository: PhotosRepository,
        private val favoritesRepository: FavoritesRepository,
        private val glide: RequestManager,
) : ViewModel(), FavoritesRepository.Listener {

    private val showPhotoInfoMutableEvent = MutableLiveData<Photo?>()
    val showPhotoInfoEvent: LiveData<Photo?> = showPhotoInfoMutableEvent

    private val showPhotoMutableEvent = MutableLiveData<Bitmap>()
    val showPhotoEvent: LiveData<Bitmap> = showPhotoMutableEvent

    private val favoriteInitMutableEvent = MutableLiveData<Event<Boolean>>()
    val favoriteInitEvent: LiveData<Event<Boolean>> = favoriteInitMutableEvent

    private val favoriteChangedMutableLiveData = MutableLiveData<Boolean>()
    val favoriteChangedLiveData: LiveData<Boolean> = favoriteChangedMutableLiveData

    private var photo = savedStateHandle.get<Photo>(Photo.KEY)

    init {
        viewModelScope.launch {
            /** Don't use FavoritesRepository.isFavorite(Photo) here because that method is using Photo.favorite flag.
             * Photo.favorite flag will be always false after using parcelable. */
            photo?.let { photo ->
                favoriteInitMutableEvent.value = Event(favoritesRepository.isFavoriteFromDatabase(photo))
            }
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

        if (photo == null) {
            return
        }

        if (photo!!.isLocalPhoto()) {
            loadLocalPhoto()
        } else {
            glide.asBitmap()
                    .load(photo!!.getPhotoFullPreviewUrl())
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onLoadStarted(placeholder: Drawable?) {
                            glide.asBitmap()
                                    .load(photo!!.getPhotoPreviewUrl())
                                    .into(object : CustomTarget<Bitmap>() {
                                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                                            if (!fullPhotoAlreadyLoaded) {
                                                showPhotoMutableEvent.value = bitmap
                                            }
                                        }

                                        override fun onLoadCleared(placeholder: Drawable?) { }
                                    })
                        }

                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                            showPhotoMutableEvent.value = bitmap
                            fullPhotoAlreadyLoaded = true
                        }

                        override fun onLoadCleared(placeholder: Drawable?) { }
                    })

            showPhotoInfoMutableEvent.postValue(photo)
        }
    }

    private fun loadLocalPhoto() {
        if (photo == null) {
            return
        }

        glide.asBitmap()
                .load(photo!!.getPhotoPreviewUrl())
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        showPhotoMutableEvent.value = bitmap
                    }

                    override fun onLoadCleared(placeholder: Drawable?) { }
                })

        viewModelScope.launch {
            try {
                photosRepository.getPhoto(photo!!.getPhotoId(), photo!!.getPhotoSource())?.let { photo ->
                    this@PhotoViewModel.photo = photo

                    glide.asBitmap()
                            .load(photo.getPhotoFullPreviewUrl())
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                                    showPhotoMutableEvent.value = bitmap
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {}
                            })

                    showPhotoInfoMutableEvent.postValue(photo)

                } ?: run {
                    // TODO Error loading photo
                }

            } catch (e: Exception) {
                // TODO Error loading photo
            }
        }
    }

    fun setWallpaper(fragmentManager: FragmentManager) {
        photo?.let { photo ->
            SetWallpaperDialog.newInstance(photo).show(fragmentManager)
        }
    }

    fun downloadPhotoAndSave() {
        photo?.let { photo ->
            App.instance.downloadPhotoAndSaveToPictures(photo.getPhotoDownloadUrl())
            App.instance.postGlobalMessage(App.instance.getString(R.string.downloading_photo))
        }
    }

    fun invertFavorite() {
        photo?.let(favoritesRepository::invertFavorite)
    }

    fun createShareIntent(): Intent? {
        return photo?.createShareIntent()
    }

    fun openAuthorUrl() {
        photo?.getPhotoPhotographerUrl()?.let(App.instance::openUrl)
    }

    fun openPhotoSource() {
        photo?.getPhotoShareUrl()?.let(App.instance::openUrl)
    }
}