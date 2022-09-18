package com.github.sikv.photo.list.ui

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sikv.photos.common.ui.LoadingErrorView
import com.google.android.material.color.MaterialColors

fun RecyclerView.setItemLayoutType(itemLayoutType: PhotoItemLayoutType) {
    layoutManager = GridLayoutManager(context, itemLayoutType.spanCount)

    val verticalPadding = context.resources.getDimension(itemLayoutType.recyclerVerticalPadding).toInt()
    setPadding(0, verticalPadding, 0, verticalPadding)

    // Invalidate view holders
    adapter = adapter
}

fun View.favoriteAnimation() {
    startAnimation(getScaleAnimation(0F, 1.1F, 0F, 1.1F))
}

private fun getScaleAnimation(fromX: Float, toX: Float, fromY: Float, toY: Float, duration: Long = 200): ScaleAnimation {
    val scaleAnimation = ScaleAnimation(fromX, toX, fromY, toY,
        Animation.RELATIVE_TO_SELF, 0.5F,
        Animation.RELATIVE_TO_SELF, 0.5F)

    scaleAnimation.duration = duration

    return scaleAnimation
}

fun LoadingErrorView.updateLoadState(loadState: CombinedLoadStates) {
    visibility = when (loadState.refresh) {
        is LoadState.NotLoading -> View.GONE
        is LoadState.Loading -> View.GONE
        is LoadState.Error -> View.VISIBLE
    }
}
