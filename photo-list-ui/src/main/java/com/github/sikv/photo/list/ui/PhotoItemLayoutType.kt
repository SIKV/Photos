package com.github.sikv.photo.list.ui

import androidx.annotation.DimenRes
import androidx.annotation.LayoutRes
import com.github.sikv.photos.domain.ListLayout

enum class PhotoItemLayoutType(
    @LayoutRes val layout: Int,
    @DimenRes val recyclerVerticalPadding: Int,
    val spanCount: Int
) {
    FULL(
        R.layout.item_photo_full,
        R.dimen.photoRecyclerVerticalPadding,
        ListLayout.LIST.spanCount
    ),

    MIN(
        R.layout.item_photo_min,
        R.dimen.photoGridRecyclerVerticalPadding,
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
