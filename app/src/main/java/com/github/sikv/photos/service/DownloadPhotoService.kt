package com.github.sikv.photos.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.IBinder
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.data.Event
import com.github.sikv.photos.util.DownloadPhotoState
import com.github.sikv.photos.util.PhotoManager
import kotlinx.coroutines.*
import javax.inject.Inject

class DownloadPhotoService : Service() {

    companion object {
        private const val ACTION_DOWNLOAD_PHOTO = "action_download_photo"
        private const val ACTION_CANCEL = "action_cancel"

        private const val KEY_PHOTO_URL = "key_photo_url"

        fun startServiceActionDownload(context: Context, photoUrl: String) {
            val intent = Intent(context, DownloadPhotoService::class.java)

            intent.action = ACTION_DOWNLOAD_PHOTO
            intent.putExtra(KEY_PHOTO_URL, photoUrl)

            context.startService(intent)
        }

        fun startServiceActionCancel(context: Context) {
            val intent = Intent(context, DownloadPhotoService::class.java)

            intent.action = ACTION_CANCEL

            context.startService(intent)
        }
    }

    private val serviceJob = Job()
    private val uiScore = CoroutineScope(Dispatchers.Main + serviceJob)

    @Inject
    lateinit var photoManager: PhotoManager

    @Inject
    lateinit var glide: RequestManager

    init {
        App.instance.appComponent.inject(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_DOWNLOAD_PHOTO -> {
                downloadAndSavePhoto(intent.getStringExtra(KEY_PHOTO_URL))
            }

            ACTION_CANCEL -> {
                serviceJob.cancel()

                postMessage(getString(R.string.photo_downloading_canceled))
                updateDownloadPhotoState(DownloadPhotoState.CANCELED)
            }
        }

        return START_NOT_STICKY
    }

    private fun downloadAndSavePhoto(photoUrl: String) {
        postMessage(getString(R.string.downloading_photo))
        updateDownloadPhotoState(DownloadPhotoState.DOWNLOADING_PHOTO)

        Glide.with(this).asBitmap()
                .load(photoUrl)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        uiScore.launch {
                            savePhoto(bitmap)?.let { uri ->
                                photoManager.savePhotoUri(this@DownloadPhotoService, uri)

                                updateDownloadPhotoState(DownloadPhotoState.PHOTO_READY)

                            } ?: run {
                                postMessage(getString(R.string.error_downloading_photo))
                                updateDownloadPhotoState(DownloadPhotoState.ERROR_DOWNLOADING_PHOTO)
                            }
                        }
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)

                        postMessage(getString(R.string.error_downloading_photo))
                        updateDownloadPhotoState(DownloadPhotoState.ERROR_DOWNLOADING_PHOTO)
                    }
                })
    }

    private suspend fun savePhoto(photo: Bitmap): Uri? {
        return withContext(Dispatchers.IO) {
            photoManager.savePhoto(this@DownloadPhotoService, photo)
        }
    }

    private fun postMessage(message: String) {
        if (App.instance.messageLiveData.hasActiveObservers()) {
            App.instance.messageLiveData.postValue(Event(message))
        }
    }

    private fun updateDownloadPhotoState(state: DownloadPhotoState) {
        App.instance.downloadPhotoStateLiveData.value = state
    }
}