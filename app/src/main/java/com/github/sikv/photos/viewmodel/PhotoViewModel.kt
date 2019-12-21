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
import com.github.sikv.photos.database.FavoritePhotoEntity
import com.github.sikv.photos.database.FavoritesDao
import com.github.sikv.photos.manager.PhotoManager
import com.github.sikv.photos.model.PexelsPhoto
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.UnsplashPhoto
import com.github.sikv.photos.util.DownloadPhotoState
import com.github.sikv.photos.util.Utils
import com.github.sikv.photos.util.subscribeAsync
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.properties.Delegates

class PhotoViewModel(
        application: Application,
        private var photo: Photo
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    @Inject
    lateinit var favoritesDao: FavoritesDao

    @Inject
    lateinit var photoManager: PhotoManager

    @Inject
    lateinit var glide: RequestManager

    // TODO Refactor
    private var favorited: Boolean by Delegates.observable(false) { _, _, newValue ->
        favoriteChangedLiveData.value = Event(newValue)
    }

    // TODO Refactor
    var favoriteChangedLiveData: MutableLiveData<Event<Boolean>>
        private set

    var photoReadyLiveData: MutableLiveData<Event<Photo?>>
        private set

    val downloadPhotoInProgressLiveData: LiveData<Boolean> = Transformations.map(App.instance.downloadPhotoStateLiveData) { state ->
        state == DownloadPhotoState.DOWNLOADING_PHOTO
    }

    val downloadPhotoStateLiveData = App.instance.downloadPhotoStateLiveData

    init {
        photoReadyLiveData = MutableLiveData()
        favoriteChangedLiveData = MutableLiveData()

        App.instance.appComponent.inject(this)

        initFavorited()
    }

    override fun onCleared() {
        super.onCleared()

        viewModelJob.cancel()
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

    fun favorite() {
        favorited = !favorited

        val favoritePhoto = FavoritePhotoEntity.fromPhoto(photo)

        GlobalScope.launch {
            if (favorited) {
                favoritesDao.insert(favoritePhoto)
            } else {
                favoritesDao.delete(favoritePhoto)
            }
        }
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

    private fun initFavorited() {
        uiScope.launch {
            favorited = getPhotoFromFavoritesDatabase() != null
        }
    }

    private suspend fun getPhotoFromFavoritesDatabase(): Photo? {
        return withContext(Dispatchers.IO) {
            favoritesDao.getById(photo.getPhotoId())
        }
    }
}