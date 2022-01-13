package com.github.sikv.photos.ui.custom

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ScrollView
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.github.sikv.photos.R
import com.github.sikv.photos.util.setVisibilityAnimated

class PhotoItemLoadingView : ScrollView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var animator = ObjectAnimator.ofFloat(this, View.ALPHA, 0.5f, 1f)
        .apply {
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            duration = 950
        }

    init {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_photo_full_loading, this, false)

        addView(view)

        animator.start()
    }

    fun updateLoadState(loadState: CombinedLoadStates) {
        when (loadState.refresh) {
            is LoadState.NotLoading -> setVisibilityAnimated(View.GONE)
            is LoadState.Loading -> setVisibilityAnimated(View.VISIBLE)
            is LoadState.Error -> setVisibilityAnimated(View.GONE)
        }
    }
}
