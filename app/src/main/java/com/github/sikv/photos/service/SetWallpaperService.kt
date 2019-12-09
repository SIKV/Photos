package com.github.sikv.photos.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.IBinder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.data.Event
import com.github.sikv.photos.util.PhotoManager
import com.github.sikv.photos.util.SetWallpaperState
import kotlinx.coroutines.*

class SetWallpaperService : Service() {

    companion object {
        private const val ACTION_SET_WALLPAPER = "action_set_wallpaper"
        private const val ACTION_CANCEL = "action_cancel"

        private const val KEY_PHOTO_URL = "key_photo_url"

        fun startService(context: Context, photoUrl: String) {
            val intent = Intent(context, SetWallpaperService::class.java)

            intent.action = ACTION_SET_WALLPAPER
            intent.putExtra(KEY_PHOTO_URL, photoUrl)

            context.startService(intent)
        }
    }

    private val serviceJob = Job()
    private val uiScore = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SET_WALLPAPER -> {
                downloadPhotoAndSetWallpaper(intent.getStringExtra(KEY_PHOTO_URL))
            }

            ACTION_CANCEL -> {
                serviceJob.cancel()

                // TODO Cancel
            }
        }

        return START_NOT_STICKY
    }

    private fun downloadPhotoAndSetWallpaper(photoUrl: String) {
        postMessage(getString(R.string.downloading_photo))
        updateSetWallpaperState(SetWallpaperState.DOWNLOADING_PHOTO)

        Glide.with(this).asBitmap()
                .load(photoUrl)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        uiScore.launch {
                            val uri = savePhoto(bitmap)

                            // TODO Save URI

                            updateSetWallpaperState(SetWallpaperState.PHOTO_READY)
                        }
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)

                        postMessage(getString(R.string.error_downloading_photo))
                        updateSetWallpaperState(SetWallpaperState.ERROR_DOWNLOADING_PHOTO)
                    }
                })
    }

    private suspend fun savePhoto(photo: Bitmap): Uri? {
        return withContext(Dispatchers.IO) {
            PhotoManager.savePhoto(this@SetWallpaperService, photo)
        }
    }

    private fun postMessage(message: String) {
        if (App.instance.messageLiveData.hasActiveObservers()) {
            App.instance.messageLiveData.postValue(Event(message))
        }
    }

    private fun updateSetWallpaperState(state: SetWallpaperState) {
        App.instance.setWallpaperStateLiveData.value = state
    }
}