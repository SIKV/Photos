package com.github.sikv.photos.ui.custom

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.ScrollView

class ScrollableImageView : FrameLayout {

    private var scrollView: FrameLayout
    private var imageView: ImageView

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        scrollView = createScrollView()
        imageView = createImageView()

        addView(scrollView)
        scrollView.addView(imageView)
    }

    public fun setImageBitmap(bitmap: Bitmap) {
        imageView.setImageBitmap(bitmap)

        imageView.apply {
            post {
                if (isPortraitOrientation()) {
                    val scrollToX = (left + right - scrollView.width) / 2
                    (scrollView as HorizontalScrollView).smoothScrollTo(scrollToX, 0)
                } else {
                    val scrollToY = (top + bottom - scrollView.height) / 2
                    (scrollView as ScrollView).smoothScrollTo(0, scrollToY)
                }
            }
        }
    }

    private fun createScrollView(): FrameLayout {
        return if (isPortraitOrientation()) {
            HorizontalScrollView(this.context).apply {
                isHorizontalScrollBarEnabled = false
            }
        } else {
            ScrollView(this.context).apply {
                isHorizontalScrollBarEnabled = false
                isVerticalScrollBarEnabled = false
            }
        }
    }

    private fun createImageView(): ImageView {
        val imageView = ImageView(this.context)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.adjustViewBounds = true

        return imageView
    }

    private fun isPortraitOrientation() =
            resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}