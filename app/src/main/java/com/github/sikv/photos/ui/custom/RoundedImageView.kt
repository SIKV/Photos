package com.github.sikv.photos.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.ImageView
import com.github.sikv.photos.R

class RoundedImageView : ImageView {

    private var radius = 0F

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.RoundedImageView, 0, 0).apply {
            radius = getDimension(R.styleable.RoundedImageView_radius, 0F)
            recycle()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val clipPath = Path()
        val rect = RectF(0F, 0F, width.toFloat(), height.toFloat())

        clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW)
        canvas.clipPath(clipPath)

        super.onDraw(canvas)
    }
}