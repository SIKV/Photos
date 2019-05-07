package com.github.sikv.photos.util

import android.support.annotation.ColorRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat


fun Snackbar.setBackgroundColor(@ColorRes color: Int): Snackbar {
    this.view.setBackgroundColor(ContextCompat.getColor(context, color))
    return this
}