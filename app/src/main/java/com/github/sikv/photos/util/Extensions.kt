package com.github.sikv.photos.util

import androidx.annotation.ColorRes
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat


fun Snackbar.setBackgroundColor(@ColorRes color: Int): Snackbar {
    this.view.setBackgroundColor(ContextCompat.getColor(context, color))
    return this
}