package com.github.sikv.photos.data

import android.content.Intent
import com.github.sikv.photos.domain.Photo

fun Photo.createShareIntent(): Intent {
    val intent = Intent()

    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_TEXT, getPhotoShareUrl())
    intent.type = "text/plain"

    return intent
}
