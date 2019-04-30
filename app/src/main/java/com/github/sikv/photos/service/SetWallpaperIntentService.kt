package com.github.sikv.photos.service

import android.annotation.TargetApi
import android.app.IntentService
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import com.github.sikv.photos.App
import com.github.sikv.photos.Event
import com.github.sikv.photos.R
import java.net.URL


class SetWallpaperIntentService : IntentService("SetWallpaperIntentService") {

    companion object {
        const val PHOTO_URL = "photo_url"
        const val WHICH = "which"
    }

    override fun onHandleIntent(intent: Intent?) {
        val photoUrl = intent?.getStringExtra(PHOTO_URL)
        val which = intent?.getIntExtra(WHICH, -1)

        if (photoUrl != null && which != null) {
            setWallpaper(photoUrl, which)
        } else {
            postMessage(applicationContext.getString(R.string.error_setting_wallpaper))
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun setWallpaper(photoUrl: String, which: Int) {
        postMessage(applicationContext.getString(R.string.setting_wallpaper))

        val url = URL(photoUrl)
        val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())

        val wallpaperManager = WallpaperManager.getInstance(applicationContext)

        if (wallpaperManager.isSetWallpaperAllowed) {
            wallpaperManager.setBitmap(bitmap, null, true, which)

            postMessage(applicationContext.getString(R.string.wallpaper_set))

        } else {
            postMessage(applicationContext.getString(R.string.error_setting_wallpaper))
        }
    }

    private fun postMessage(message: String) {
        if (App.instance?.messageLiveData?.hasActiveObservers() == true) {
            App.instance?.messageLiveData?.postValue(Event(message))
        }
    }
}