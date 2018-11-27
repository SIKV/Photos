package com.github.sikv.photos.viewmodel

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


class PhotoViewModel(
        application: Application,
        private val photo: Photo

) : AndroidViewModel(application) {

    private val favoritesDatabase: FavoritesDatabase

    init {
        favoritesDatabase = FavoritesDatabase.getInstance(getApplication())
    }

    fun loadPhoto(glide: RequestManager): LiveData<Event<Bitmap>> {
        val photoLoadedEvent = MutableLiveData<Event<Bitmap>>()
        var photoLoaded = false

        glide.asBitmap()
                .load(photo.urls.regular)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onLoadStarted(placeholder: Drawable?) {
                        glide.asBitmap()
                                .load(photo.urls.small)
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
        InsertAsyncTask(favoritesDatabase).execute(PhotoData(photo.id, photo.urls.small))
    }

    fun createShareIntent(): Intent {
        val shareIntent = Intent()

        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, photo.links.html)
        shareIntent.type = "text/plain"

        return shareIntent
    }

    private class InsertAsyncTask internal constructor(
            private val db: FavoritesDatabase

    ) : AsyncTask<PhotoData, Void, Void>() {

        override fun doInBackground(vararg params: PhotoData): Void? {
            db.photoDao().insert(params[0])
            return null
        }
    }
}