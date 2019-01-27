package com.github.sikv.photos.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.github.sikv.photos.Event
import com.github.sikv.photos.database.FavoritesDatabase
import com.github.sikv.photos.database.PhotoData
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.Utils
import kotlin.properties.Delegates

@SuppressLint("StaticFieldLeak")
class PhotoViewModel(
        application: Application,
        private val photo: Photo

) : AndroidViewModel(application) {

    private val favoritesDatabase: FavoritesDatabase = FavoritesDatabase.getInstance(getApplication())

    private var favorited: Boolean by Delegates.observable(false) { property, oldValue, newValue ->
        favoriteChangedEvent.value = Event(newValue)
    }

    var favoriteChangedEvent: MutableLiveData<Event<Boolean>>
        private set

    init {
        favoriteChangedEvent = MutableLiveData()

        object : AsyncTask<String, Void, Boolean>() {
            override fun doInBackground(vararg p0: String?): Boolean {
                (favoritesDatabase.photoDao().get(photo.getPhotoId()))?.let {
                    return true
                } ?: run {
                    return false
                }
            }

            override fun onPostExecute(result: Boolean?) {
                super.onPostExecute(result)

                result?.let {
                    favorited = it
                }
            }
        }.execute(photo.getPhotoId())
    }

    fun loadPhoto(glide: RequestManager): LiveData<Event<Bitmap>> {
        val photoLoadedEvent = MutableLiveData<Event<Bitmap>>()
        var photoLoaded = false

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

        return photoLoadedEvent
    }

    fun favorite() {
        favorited = !favorited
        FavoriteAsyncTask(favoritesDatabase, favorited).execute(PhotoData(photo.getPhotoId(), photo.getSmallUrl()))
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

    private class FavoriteAsyncTask internal constructor(
            private val db: FavoritesDatabase,
            private val favorite: Boolean

    ) : AsyncTask<PhotoData, Void, Void>() {

        override fun doInBackground(vararg params: PhotoData): Void? {
            if (favorite) {
                db.photoDao().insert(params[0])
            } else {
                db.photoDao().delete(params[0])
            }
            return null
        }
    }
}