package com.github.sikv.photos.util

import android.annotation.TargetApi
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.os.Build
import com.github.sikv.photos.service.SetWallpaperIntentService

@TargetApi(Build.VERSION_CODES.N)
object CustomWallpaperManager {

    enum class Which(val which: Int) {
        HOME(WallpaperManager.FLAG_SYSTEM),
        LOCK(WallpaperManager.FLAG_LOCK),
        BOTH(WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK)
    }

    fun setWallpaper(context: Context, photoUrl: String, which: Which) {
        Intent(context, SetWallpaperIntentService::class.java).also { intent ->
            intent.putExtra(SetWallpaperIntentService.PHOTO_URL, photoUrl)
            intent.putExtra(SetWallpaperIntentService.WHICH, which.which)

            context.startService(intent)
        }
    }
}