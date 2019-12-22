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
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.github.sikv.photos.App
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.data.Event
import com.github.sikv.photos.manager.FavoritesManager
import com.github.sikv.photos.manager.PhotoManager
import com.github.sikv.photos.model.PexelsPhoto
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.UnsplashPhoto
import com.github.sikv.photos.util.DownloadPhotoState
import com.github.sikv.photos.util.Utils
import com.github.sikv.photos.util.subscribeAsync
import javax.inject.Inject

class PhotoViewModel(
        application: Application,
        private var photo: Photo
) : AndroidViewModel(application), FavoritesManager.Callback {

    @Inject
    lateinit var photoManager: PhotoManager

    @Inject
    lateinit var favoritesManager: FavoritesManager

    @Inject
    lateinit var glide: RequestManager

    var photoReadyLiveData: MutableLiveData<Event<Photo?>>
        private set

    private val favoriteInitMutableLiveData = MutableLiveData<Event<Boolean>>()
    val favoriteInitLiveData = favoriteInitMutableLiveData

    private val favoriteChangedMutableLiveData = MutableLiveData<Boolean>()
    val favoriteChangedLiveData: LiveData<Boolean> = favoriteChangedMutableLiveData

    val downloadPhotoInProgressLiveData: LiveData<Boolean> = Transformations.map(App.instance.downloadPhotoStateLiveData) { state ->
        state == DownloadPhotoState.DOWNLOADING_PHOTO
    }

    val downloadPhotoStateLiveData = App.instance.downloadPhotoStateLiveData

    init {
        App.instance.appComponent.inject(this)

        photoReadyLiveData = MutableLiveData()

        favoriteInitMutableLiveData.postValue(Event(favoritesManager.getFavoriteFlagFor(photo)))

        favoritesManager.subscribe(this)
    }

    override fun onCleared() {
        super.onCleared()

        favoritesManager.unsubscribe(this)
    }

    override fun onFavoriteChanged(photo: Photo, favorite: Boolean) {
        favoriteChangedMutableLiveData.postValue(favorite)
    }

    fun loadPhoto(): LiveData<Event<Bitmap>> {
        val photoLoadedEvent = MutableLiveData<Event<Bitmap>>()
        var photoLoaded = false

        fun loadFullSizePhoto() {
            glide.asBitmap()
                    .load(photo.getNormalUrl())
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                            photoLoadedEvent.value = Event(bitmap)
                            photoLoaded = true
                        }
                    })
        }

        if (photo.isLocalPhoto()) {
            glide.asBitmap()
                    .load(photo.getSmallUrl())
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                            photoLoadedEvent.value = Event(bitmap)
                        }
                    })

            when (photo.getSource()) {
                UnsplashPhoto.SOURCE -> {
                    ApiClient.INSTANCE.unsplashClient.getPhoto(photo.getPhotoId())
                            .subscribeAsync({
                                this@PhotoViewModel.photo = it
                                loadFullSizePhoto()

                                photoReadyLiveData.value = Event(it)
                            }, {
                                // TODO Handle Error
                            })
                }

                PexelsPhoto.SOURCE -> {
                    ApiClient.INSTANCE.pexelsClient.getPhoto(photo.getPhotoId())
                            .subscribeAsync({
                                this@PhotoViewModel.photo = it
                                loadFullSizePhoto()

                                photoReadyLiveData.value = Event(it)
                            }, {
                                // TODO Handle Error
                            })
                }
            }

        } else {
            glide.asBitmap()
                    .load(photo.getNormalUrl())
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onLoadStarted(placeholder: Drawable?) {
                            glide.asBitmap()
                                    .load(photo.getSmallUrl())
                                    .into(object : SimpleTarget<Bitmap>() {
                                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                                            if (!photoLoaded) {
                                                photoLoadedEvent.value = Event(bitmap)
                                            }
                                        }
                                    })
                        }

                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                            photoLoadedEvent.value = Event(bitmap)
                            photoLoaded = true
                        }
                    })

            photoReadyLiveData.value = Event(photo)
        }

        return photoLoadedEvent
    }

    fun setWallpaper() {
        photoManager.getSavedPhotoUri(getApplication())?.let { uri ->
            photoManager.startSetWallpaper(getApplication(), uri)
        } ?: run {

            // TODO Handle
        }
    }

    fun invertFavorite() {
        favoritesManager.invertFavorite(photo)
    }

    fun createShareIntent(): Intent {
        val shareIntent = Intent()

        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, photo.getShareUrl())
        shareIntent.type = "text/plain"

        return shareIntent
    }

    fun openAuthorUrl() {
        photo.getPhotographerUrl()?.let {
            Utils.openUrl(getApplication(), it)
        }
    }

    fun openPhotoSource() {
        Utils.openUrl(getApplication(), photo.getSourceUrl())
    }

    fun setWallpaper(activity: Activity) {
        photoManager.downloadPhoto(activity, photo.getLargeUrl())
    }
}