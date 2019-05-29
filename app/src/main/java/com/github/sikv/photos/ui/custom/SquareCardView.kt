package com.github.sikv.photos.ui.custom

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet


class SquareCardView(context: Context, attrs: AttributeSet?) : CardView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}