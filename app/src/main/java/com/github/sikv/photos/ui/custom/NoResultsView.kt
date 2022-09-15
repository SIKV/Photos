package com.github.sikv.photos.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.github.sikv.photos.R

class NoResultsView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        applyAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        applyAttrs(attrs)
    }

    private val imageView: ImageView
    private val titleText: TextView
    private val descriptionText: TextView
    private val actionButton: Button

    init {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.view_no_results, this, false)

        imageView = view.findViewById(R.id.imageView)
        titleText = view.findViewById(R.id.titleText)
        descriptionText = view.findViewById(R.id.descriptionText)
        actionButton = view.findViewById(R.id.actionButton)

        addView(view)
    }

    fun setActionButtonClickListener(listener: OnClickListener?) {
        actionButton.setOnClickListener(listener)
    }

    private fun applyAttrs(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.NoResultsView,
            0,
            0
        ).apply {
            try {
                val dataSourceType = getInteger(R.styleable.NoResultsView_dataSourceType, -1)
                setDataSourceType(dataSourceType)
            } finally {
                recycle()
            }
        }
    }

    private fun setDataSourceType(dataSourceType: Int) {
        when (dataSourceType) {
            // Search
            0 -> {
                imageView.setImageResource(R.drawable.ic_search_24dp)
                titleText.setText(R.string.no_results_found)
                descriptionText.setText(R.string.no_results_found_description)
                actionButton.isVisible = false
            }
            // Favorites
            1 -> {
                // TODO WILL BE MOVE TO :common-ui
//                imageView.setImageResource(R.drawable.ic_favorite_border_24dp)
                titleText.setText(R.string.no_favorites)
                descriptionText.setText(R.string.no_favorites_description)
                actionButton.isVisible = false
            }
            // Recommendations
            2 -> {
                imageView.setImageResource(R.drawable.ic_broken_image_24dp)
                titleText.setText(R.string.no_recommendations)
                descriptionText.setText(R.string.no_recommendations_description)
                actionButton.isVisible = true
                actionButton.setText(R.string.refresh)
            }
        }
    }
}
