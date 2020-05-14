package com.github.sikv.photos.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.event.Event
import com.github.sikv.photos.model.DummyPhoto
import com.github.sikv.photos.model.PexelsPhoto
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.UnsplashPhoto
import com.github.sikv.photos.service.DownloadPhotoService
import com.github.sikv.photos.util.downloadPhotoAndSaveToPictures
import com.github.sikv.photos.util.openUrl
import com.github.sikv.photos.util.startSetWallpaperActivity
import com.github.sikv.photos.util.subscribeAsync
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class PhotoViewModel(
        application: Application,
        private var photo: Photo
) : AndroidViewModel(application), FavoritesRepository.Callback {

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

    val downloadPhotoStateEvent = DownloadPhotoService.stateEvent
    val setWallpaperResultStateEvent = App.instance.setWallpaperResultStateEvent

    init {
        App.instance.appComponent.inject(this)

        GlobalScope.launch(Dispatchers.IO) {
            /** Don't use FavoritesRepository.isFavorite(Photo) here because that method is using Photo.favorite flag.
             * Photo.favorite flag will be always false after using parcelable. */
            favoriteInitMutableEvent.postValue(Event(favoritesRepository.isFavoriteFromDatabase(photo)))
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

        when (photo.getPhotoSource()) {
            UnsplashPhoto.SOURCE -> ApiClient.INSTANCE.unsplashClient.getPhoto(photo.getPhotoId())
            PexelsPhoto.SOURCE -> ApiClient.INSTANCE.pexelsClient.getPhoto(photo.getPhotoId())

            else -> Single.just(DummyPhoto())

        }.subscribeAsync({
            this@PhotoViewModel.photo = it

            glide.asBitmap()
                    .load(photo.getPhotoFullPreviewUrl())
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                            showPhotoMutableEvent.value = Event(bitmap)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) { }
                    })

            showPhotoInfoMutableEvent.postValue(Event(it))

        }, {
            // TODO Handle error
        })
    }

    fun setWallpaperFromUri(uri: Uri) {
        getApplication<Application>().startSetWallpaperActivity(uri)
    }

    fun downloadPhotoAndSave() {
        getApplication<Application>().apply {
            applicationContext.downloadPhotoAndSaveToPictures(photo.getPhotoDownloadUrl())

            App.instance.postGlobalMessage(getString(R.string.downloading_photo))
        }
    }

    fun downloadPhoto() {
        getApplication<Application>().let {
            DownloadPhotoService.startService(it,
                    action = DownloadPhotoService.ACTION_DOWNLOAD,
                    photoUrl = photo.getPhotoDownloadUrl()
            )
        }
    }

    fun cancelPhotoDownloading() {
        getApplication<Application>().let {
            DownloadPhotoService.startService(it, action = DownloadPhotoService.ACTION_CANCEL)
        }
    }

    fun invertFavorite() {
        favoritesRepository.invertFavorite(photo)
    }

    fun createShareIntent(): Intent {
        val shareIntent = Intent()

        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, photo.getPhotoShareUrl())
        shareIntent.type = "text/plain"

        return shareIntent
    }

    fun openAuthorUrl() {
        photo.getPhotoPhotographerUrl()?.let {
            (getApplication() as? Context)?.openUrl(it)
        }
    }

    fun openPhotoSource() {
        (getApplication() as? Context)?.openUrl(photo.getSourceUrl())
    }
}