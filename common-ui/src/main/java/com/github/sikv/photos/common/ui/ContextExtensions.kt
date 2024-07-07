package com.github.sikv.photos.common.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.browser.customtabs.CustomTabsIntent

fun Context.openUrl(url: String) {
    val builder = CustomTabsIntent.Builder()
    val intent = builder.build()
    intent.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.launchUrl(this, Uri.parse(url))
}

fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri

    startActivity(intent)
}

fun Context.copyText(label: String, text: String) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(label, text)

    clipboardManager.setPrimaryClip(clipData)
}
