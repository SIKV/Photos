package com.github.sikv.photos.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.enumeration.SetWallpaperState
import com.github.sikv.photos.event.Event
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.startSetWallpaperActivity
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

data class StateWithData(val state: SetWallpaperState, val data: Any?)

class SetWallpaperViewModel(
        application: Application,
        private var photo: Photo
) : AndroidViewModel(application) {

    companion object {
        private const val KEY_PHOTO_URI = "photoUri"
    }

    @Inject
    lateinit var glide: RequestManager

    private val stateMutableEvent = MutableLiveData<Event<StateWithData>>()
    val stateEvent: LiveData<Event<StateWithData>> = stateMutableEvent

    init {
        App.instance.appComponent.inject(this)

        setWallpaper()
    }

    fun setWallpaperFromUri(uri: Uri) {
        getApplication<Application>().startSetWallpaperActivity(uri)
    }

    fun setWallpaper() {
        updateState(SetWallpaperState.DOWNLOADING_PHOTO)

        val url = photo.getPhotoDownloadUrl()

        glide.asBitmap()
                .load(url)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        viewModelScope.launch {
                            savePhoto(bitmap)?.let { uri ->
                                savePhotoUri(uri)
                                updateState(SetWallpaperState.PHOTO_READY, data = getSavedPhotoUri())

                            } ?: run {
                                updateState(SetWallpaperState.ERROR_DOWNLOADING_PHOTO)
                            }
                        }
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)

                        updateState(SetWallpaperState.ERROR_DOWNLOADING_PHOTO)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) { }
                })
    }

    private fun savePhoto(photo: Bitmap): Uri? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

        val byteArray = byteArrayOutputStream.toByteArray()

        val context = getApplication<Application>()

        val filename = "photo.jpeg"
        val file = File(context.filesDir, filename)

        file.writeBytes(byteArray)

        return FileProvider.getUriForFile(context, context.getString(R.string._file_provider), file)
    }

    private fun getSavedPhotoUri(): Uri? {
        App.instance.getPrivatePreferences().getString(KEY_PHOTO_URI, null)?.let { uriStr ->
            return Uri.parse(uriStr)
        } ?: run {
            return null
        }
    }

    private fun savePhotoUri(uri: Uri) {
        val editor = App.instance.getPrivatePreferences()
                .edit()

        editor.putString(KEY_PHOTO_URI, uri.toString())
        editor.apply()
    }

    private fun updateState(state: SetWallpaperState, data: Any? = null) {
        stateMutableEvent.postValue(Event(StateWithData(state, data)))
    }
}