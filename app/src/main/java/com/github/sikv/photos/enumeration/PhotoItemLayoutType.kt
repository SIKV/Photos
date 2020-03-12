package com.github.sikv.photos.enumeration

import androidx.annotation.LayoutRes
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.util.SPAN_COUNT_GRID
import com.github.sikv.photos.util.SPAN_COUNT_LIST

enum class PhotoItemLayoutType(
        @LayoutRes val layout: Int,
        val recyclerVerticalPadding: Int,
        val spanCount: Int
) {
    FULL(R.layout.item_photo_full,
            App.instance.resources.getDimension(R.dimen.photoRecyclerVerticalPadding).toInt(),
            SPAN_COUNT_LIST
    ),

    MIN(R.layout.item_photo_min,
            App.instance.resources.getDimension(R.dimen.photoGridRecyclerVerticalPadding).toInt(),
            SPAN_COUNT_GRID
    );

    companion object {
        fun findBySpanCount(spanCount: Int): PhotoItemLayoutType {
            return if (spanCount == SPAN_COUNT_GRID) {
                MIN
            } else {
                FULL
            }
        }
    }
}