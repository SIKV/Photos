package com.github.sikv.photos.util

import android.app.Activity
import android.content.pm.ShortcutManager
import android.os.Build

fun String.reportShortcutUsed(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
        val shortcutManager = activity.getSystemService(ShortcutManager::class.java) as ShortcutManager
        shortcutManager.reportShortcutUsed(this)
    }
}
