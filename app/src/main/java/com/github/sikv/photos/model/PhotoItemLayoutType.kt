package com.github.sikv.photos.model

import androidx.annotation.LayoutRes
import com.github.sikv.photos.App
import com.github.sikv.photos.R

enum class PhotoItemLayoutType(
    @LayoutRes val layout: Int,
    val recyclerVerticalPadding: Int,
    val spanCount: Int
) {
    FULL(
        R.layout.item_photo_full,
        App.instance.resources.getDimension(R.dimen.photoRecyclerVerticalPadding).toInt(),
        ListLayout.LIST.spanCount
    ),

    MIN(
        R.layout.item_photo_min,
        App.instance.resources.getDimension(R.dimen.photoGridRecyclerVerticalPadding).toInt(),
        ListLayout.GRID.spanCount
    );

    companion object {
        fun findBySpanCount(spanCount: Int): PhotoItemLayoutType {
            return if (spanCount == ListLayout.GRID.spanCount) {
                MIN
            } else {
                FULL
            }
        }
    }
}
