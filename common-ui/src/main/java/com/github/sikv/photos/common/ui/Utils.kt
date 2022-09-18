package com.github.sikv.photos.common.ui

import android.content.Context
import android.graphics.Color
import com.google.android.material.color.MaterialColors

fun getAttributionPlaceholderTextColor(context: Context): Int = MaterialColors
    .getColor(context, R.attr.colorOnPrimaryContainer, Color.WHITE)

fun getAttributionPlaceholderBackgroundColor(context: Context): Int = MaterialColors
    .getColor(context, R.attr.colorPrimaryContainer, Color.BLACK)
