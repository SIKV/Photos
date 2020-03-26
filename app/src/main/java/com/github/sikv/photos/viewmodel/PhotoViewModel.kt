package com.github.sikv.photos.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.sikv.photos.App
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.enumeration.DownloadPhotoState
import com.github.sikv.photos.event.Event
import com.github.sikv.photos.model.DummyPhoto
import com.github.sikv.photos.model.PexelsPhoto
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.UnsplashPhoto
import com.github.sikv.photos.util.*
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

    val downloadPhotoInProgressLiveData: LiveData<Boolean> = Transformations.map(App.instance.downloadPhotoStateChangedLiveData) { state ->
        state == DownloadPhotoState.DOWNLOADING_PHOTO
    }

    val downloadPhotoStateChangedLiveData = App.instance.downloadPhotoStateChangedLiveData
    val setWallpaperStateChangedEvent = App.instance.setWallpaperStateChangedEvent

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

    fun setWallpaper() {
        getApplication<Application>().getSavedPhotoUri()?.let { uri ->
            getApplication<Application>().startSetWallpaperActivity(uri)
        } ?: run {

            // TODO Handle
        }
    }

    fun invertFavorite() {
        favoritesRepository.invertFavorite(photo)
    }

    fun createShareIntent(): Intent {
        val shareIntent = Intent()

        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, photo.getShareUrl())
        shareIntent.type = "text/plain"

        return shareIntent
    }

    fun openAuthorUrl() {
        photo.getPhotoPhotographerUrl()?.let {
            Utils.openUrl(getApplication(), it)
        }
    }

    fun openPhotoSource() {
        Utils.openUrl(getApplication(), photo.getSourceUrl())
    }

    fun setWallpaper(activity: Activity) {
        activity.downloadPhoto(photo.getPhotoWallpaperUrl())
    }
}