package com.github.sikv.photos.util

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar


fun Fragment.customTag(): String {
    return this::class.java.simpleName
}

fun Snackbar.setBackgroundColor(@ColorRes color: Int): Snackbar {
    this.view.setBackgroundColor(ContextCompat.getColor(context, color))
    return this
}