package com.github.sikv.photos.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.github.sikv.photos.R

class LoadingErrorView : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val tryAgainButton: Button

    init {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.view_loading_error, this, false)

        tryAgainButton = view.findViewById(R.id.tryAgainButton)

        addView(view)
    }

    fun setTryAgainClickListener(listener: OnClickListener?) {
        tryAgainButton.setOnClickListener(listener)
    }

    fun updateLoadState(loadState: CombinedLoadStates) {
        isVisible = when (loadState.refresh) {
            is LoadState.NotLoading -> false
            is LoadState.Loading -> false
            is LoadState.Error -> true
        }
    }
}
