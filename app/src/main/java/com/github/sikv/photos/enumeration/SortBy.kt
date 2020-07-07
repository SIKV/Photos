package com.github.sikv.photos.enumeration

import androidx.annotation.StringRes
import com.github.sikv.photos.R

enum class SortBy(@StringRes val text: Int) {
    DATE_ADDED_NEWEST(R.string.date_added_newest),
    DATE_ADDED_OLDEST(R.string.date_added_oldest)
}