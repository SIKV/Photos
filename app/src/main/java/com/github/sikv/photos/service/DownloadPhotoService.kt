package com.github.sikv.photos.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.sikv.photos.App
import com.github.sikv.photos.enumeration.DownloadPhotoState
import com.github.sikv.photos.event.Event
import com.github.sikv.photos.util.savePhotoInFile
import kotlinx.coroutines.*
import javax.inject.Inject

class DownloadPhotoService : Service() {

    data class StateWithData(val state: DownloadPhotoState, val data: Any?)

    companion object {
        const val ACTION_DOWNLOAD = "action_download"
        const val ACTION_CANCEL = "action_cancel"

        private const val KEY_PHOTO_URI = "key_photo_uri"

        private const val EXTRA_PHOTO_URL = "extra_photo_url"

        private val stateMutableEvent = MutableLiveData<Event<StateWithData>>()
        val stateEvent: LiveData<Event<StateWithData>> = stateMutableEvent

        fun startService(context: Context, action: String, photoUrl: String? = null) {
            val intent = Intent(context, DownloadPhotoService::class.java)

            intent.action = action
            intent.putExtra(EXTRA_PHOTO_URL, photoUrl)

            context.startService(intent)
        }
    }

    private var job: Job? = null

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
            ACTION_DOWNLOAD -> {
                downloadAndSavePhoto(intent.getStringExtra(EXTRA_PHOTO_URL))
            }

            ACTION_CANCEL -> {
                cancel()
            }
        }

        return START_NOT_STICKY
    }

    private fun cancel() {
        CoroutineScope(Dispatchers.Main).launch {
            updateState(DownloadPhotoState.CANCELING)

            job?.cancelAndJoin()

            updateState(DownloadPhotoState.CANCELED)
        }
    }

    private fun downloadAndSavePhoto(photoUrl: String?) {
        updateState(DownloadPhotoState.DOWNLOADING_PHOTO)

        glide.asBitmap()
                .load(photoUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        job = CoroutineScope(Dispatchers.Main).launch {
                            savePhoto(bitmap)?.let { uri ->
                                this@DownloadPhotoService.savePhotoUri(uri)

                                updateState(DownloadPhotoState.PHOTO_READY, data = getSavedPhotoUri())

                            } ?: run {
                                updateState(DownloadPhotoState.ERROR_DOWNLOADING_PHOTO)
                            }
                        }
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)

                        updateState(DownloadPhotoState.ERROR_DOWNLOADING_PHOTO)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) { }
                })
    }

    private suspend fun savePhoto(photo: Bitmap): Uri? {
        return withContext(Dispatchers.IO) {
            this@DownloadPhotoService.savePhotoInFile(photo)
        }
    }

    private fun updateState(state: DownloadPhotoState, data: Any? = null) {
        stateMutableEvent.postValue(Event(StateWithData(state, data)))
    }

    fun getSavedPhotoUri(): Uri? {
        App.instance.getPrivatePreferences().getString(KEY_PHOTO_URI, null)?.let { uriStr ->
            return Uri.parse(uriStr)
        } ?: run {
            return null
        }
    }

    fun savePhotoUri(uri: Uri) {
        val editor = App.instance.getPrivatePreferences()
                .edit()

        editor.putString(KEY_PHOTO_URI, uri.toString())
        editor.apply()
    }
}