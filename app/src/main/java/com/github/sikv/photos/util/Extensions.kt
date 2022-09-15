package com.github.sikv.photos.util

import android.app.Application
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel

fun AndroidViewModel.getString(@StringRes id: Int): String {
    return getApplication<Application>().resources.getString(id)
}
